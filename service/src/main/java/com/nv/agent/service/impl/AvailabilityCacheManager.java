package com.nv.agent.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.exception.ErrorStatusType;
import com.nv.platform.json.JSONMapper;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.timespec.TimeSpec;
import com.nv.platform.timespec.TimeSpecException;
import com.nv.platform.timespec.TimeSpecFactory;
import com.nv.platform.timespec.impl.TimeSpecOperations;
import com.nv.platform.timespec.impl.rules.TimeRule;
import com.nv.platform.timespec.metadata.TimeRange;
import com.nv.platform.timespec.metadata.TimeRangeList;
import com.nv.ynw.cache.CacheDao;
import com.nv.ynw.cache.CacheEntity;
import com.nv.ynw.cache.CacheException;
import com.nv.ynw.cache.CacheStatus;
import com.nv.ynw.cache.availability.AccountSchedule;
import com.nv.ynw.cache.availability.ProviderAvailability;
import com.nv.ynw.cache.availability.ProviderAvailabilityComparator;
import com.nv.ynw.cache.availability.ProviderScheduleCache;
import com.nv.ynw.cache.availability.ProviderServiceScheduleCache;
import com.nv.ynw.cache.availability.Schedule;
import com.nv.ynw.cache.availability.ScheduleStatus;
import com.nv.ynw.cache.availability.ServiceSchedule;
import com.nv.ynw.cache.availability.WorkMap;
import com.nv.ynw.cache.availability.WorkMap.BookedAppointment;
import com.nv.ynw.service.dto.Service;

/**
 * Availability cache Manager
 * @author Asha
 */
public class AvailabilityCacheManager extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(AvailabilityCacheManager.class);
	private ReadDao readDao;
	private CacheDao cacheDao;
	private JSONMapper objectMapper;
	
	private static final String get_account_schedule_details = "select new com.nv.ynw.cache.availability.AccountSchedule(account.accountId.id,account.minApmtTime,account.businessSchedule) from AccountProfileEntity as account order by account.accountId.id asc ";
	private static final String get_appmnt_duration_services = "select new com.nv.ynw.service.dto.Service(service.id,service.appmtDuration) from ServiceEntity as service where service.account.id=:param1";
	private static final String get_schedules_of_provider = "select new com.nv.ynw.cache.availability.Schedule(schedule.id,schedule.timeSlot) from ProviderScheduleEntity as schedule where schedule.provider.userProfile.id=:param1";
	private static final String get_vacations_of_provider = "select vacation.timeSpec from ProviderUnavilabilityEntity as vacation where vacation.provider.userProfile.id=:param1 and vacation.endDate>=CURDATE()";
	private static final String get_holidyas_of_acc = "select holiday.nonWorkingTimespec from HolidaysEntity as holiday where holiday.account.id=:param1 and holiday.date>=CURDATE()";
	private static final String get_provider_service_schedule = "select new com.nv.ynw.cache.availability.ServiceSchedule(pss.service.id,pss.provider.userProfile.id,pss.schedule.id) from ServiceScheduleEntity as pss where pss.provider.userProfile.id=:param1";
	private static final String get_account_count = "select count(*) from AccountProfileEntity as account";
	private static final String get_providers = "select provider.userProfile.id from ProviderEntity as provider where provider.account.id=:param1";
	private static final String get_cache_status_by_name = "from CacheEntity as cacheEntity where cacheEntity.cacheName=:param1" ;
	private  static final String get_previous_provider_service_schedules ="select prvServiceSchedule.id from  ProviderServiceScheduleCache  as prvServiceSchedule where  prvServiceSchedule.providerId=:param1 and  prvServiceSchedule.dateOfAvailability<CURDATE()";
	private  static final String get_previous_provider_schedules ="select prvSchedule.id from  ProviderScheduleCache  as prvSchedule where  prvSchedule.providerId=:param1 and prvSchedule.dateOfAvailability<CURDATE()";
	private  static final String get_previous_provider_schedules_count ="select count(*) from ProviderScheduleCache  as prvSchedule where prvSchedule.dateOfAvailability<CURDATE()";
	private  static final String get_previous_provider_schedules_count_of_acc = "select count(*) from ProviderScheduleCache  as prvSchedule where prvSchedule.dateOfAvailability<CURDATE() and prvSchedule.accountId=:param1";

/*	public AvailabilityCacheManager(ReadDao readDao, CacheDao cacheDao, JSONMapper objectMapper) {
		super();
		this.readDao = readDao;
		this.cacheDao = cacheDao;
		this.objectMapper = objectMapper;
	}*/
	/*public AvailabilityCacheManager(){
		
	}*/
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("In agent --cachemanger"+new Date());
		CacheEntity cacheEntity;
		try {
			cacheEntity = cacheDao.executeUniqueQuery(CacheEntity.class,get_cache_status_by_name,"provider-availability");
			if(cacheEntity==null){
				return;
			}
			long prevDayCachecount = cacheDao.executeUniqueQuery(Long.class, get_previous_provider_schedules_count); //get count of  cache entries of previous day
			if(prevDayCachecount==0){
				return;
			}
			cacheEntity.setCacheStatus(CacheStatus.InProcess);
			cacheDao.update(cacheEntity);
			
			
		    long count = readDao.executeUniqueQuery(Long.class, get_account_count); //get total account count

			/*
			 * Build provider availability cache for accounts(100 accounts at a time)
			 */
			int limitInc = 100;
			int end = limitInc;
			int start = 0;
			if(end <= count){
				for (; start <= count; end=end+limitInc) {
					buildProviderAvailabilityCache(start,end);
					start=end;
				}
			}else{
				buildProviderAvailabilityCache(start,end);
			}

			cacheEntity = cacheDao.executeUniqueQuery(CacheEntity.class,get_cache_status_by_name,"provider-availability");
			cacheEntity.setCacheStatus(CacheStatus.Ready);
			cacheDao.update(cacheEntity);
		} catch (CacheException | PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Build and save availabilities of all providers for a limited range of accounts
	 * @param start accounts from
	 * @param end accounts till
	 * @throws PersistenceException {@link PersistenceException}
	 * @throws CacheException {@link CacheException}
	 */
	private void buildProviderAvailabilityCache(int start,int end) 
			throws PersistenceException, CacheException {

		//List<AccountSchedule> accountSchedules = cacheManagerDao.getAccountScheduleDetails(start, end);
		List<AccountSchedule> accountSchedules= readDao.executeQuery(AccountSchedule.class, get_account_schedule_details, new int[]{start,end});  
		for (AccountSchedule accountSchedule : accountSchedules) {

			long prevDayCachecount = cacheDao.executeUniqueQuery(Long.class, get_previous_provider_schedules_count_of_acc,accountSchedule.getId()); //get count of  cache entries of previous day
			if(prevDayCachecount==0){
				return;
			}
			List<Integer> providerIds = readDao.executeQuery(Integer.class, get_providers,accountSchedule.getId());//get all providers of account
			List<String> holidayStr = readDao.executeQuery(String.class,get_holidyas_of_acc,accountSchedule.getId());//get holidays of account
			Map<Integer,Integer> serviceAppmtdurations = this.getAppmtDurationOfServices(accountSchedule.getId());//get appointment duration of all services of account
			for (Integer providerId : providerIds) {
				List<ServiceSchedule> serviceSchedules = readDao.executeQuery(ServiceSchedule.class,get_provider_service_schedule,providerId);//get all services and associated schedule of provider
				List<String> vacationStr = readDao.executeQuery(String.class,get_vacations_of_provider,providerId);// get vacations of provider

				List<ProviderAvailability> providerAvailabilities = getProviderAvailability(providerId,accountSchedule.getId(),accountSchedule.getBusinessSchedule(),
						serviceSchedules,timespecsWithSeperatorToArray(vacationStr), timespecsWithSeperatorToArray(holidayStr), accountSchedule.getMinApptTime(),serviceAppmtdurations); // get available schedules of provider in a range of minimum appointment time of account
				this.deletePrevDayAvailability(providerId);
				saveProviderSchedulesIntoCache(accountSchedule.getId(), providerAvailabilities,serviceAppmtdurations);
			}
		}
	}
	private void deletePrevDayAvailability(int providerId) throws CacheException {
		//List<ProviderServiceScheduleCache> providerServiceScheduleCaches = cacheDao.executeQuery(ProviderServiceScheduleCache.class,get_previous_provider_service_schedules, providerId);
		List<Integer> providerServiceScheduleCacheIds = cacheDao.executeQuery(Integer.class,get_previous_provider_service_schedules, providerId);
		for (Integer providerServiceScheduleCacheId : providerServiceScheduleCacheIds) {
				System.out.println("ProviderId in providerServiceScheduleCache:"+providerServiceScheduleCacheId);
				cacheDao.deleteWithId(ProviderServiceScheduleCache.class,providerServiceScheduleCacheId);
		}
		//List<ProviderScheduleCache> providerScheduleCaches =cacheDao.executeQuery(ProviderScheduleCache.class,get_previous_provider_schedules, providerId);
		List<Integer> providerScheduleCacheIds =cacheDao.executeQuery(Integer.class,get_previous_provider_schedules, providerId);
		for (Integer providerScheduleCacheId : providerScheduleCacheIds) {
			System.out.println("ProviderId in ProviderScheduleCache:"+providerScheduleCacheId);
			cacheDao.deleteWithId(ProviderScheduleCache.class,providerScheduleCacheId);
		}
		/*Statement stmt =null;
		try {
			stmt = cacheDbConnection.createStatement();
			String sqlQuery= "DELETE  FROM provider_service_availability_cache where  provider_id="+providerId+" and date<CURDATE()";
			System.out.println("sqlQuery:"+sqlQuery);
			stmt.executeUpdate(sqlQuery);
			String sql= "DELETE  FROM provider_availability_cache where  provider_id="+providerId+" and date<CURDATE()";
			System.out.println("sqlQuery:"+sql);
			stmt.executeUpdate(sql);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new NVLogFormatter("Error while retreiving account details", e));
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error(new NVLogFormatter("Error closing resultset", e));
			}
		}*/
	}
	/**
	 * Save provider schedules into cache
	 * @param accountId account id
	 * @param providerAvailabilities {@link ProviderAvailability}
	 * @param serviceAppmtdurations map with service id as key and appointment duration as value
	 * @throws CacheException {@link CacheException}
	 */
	private void saveProviderSchedulesIntoCache(int accountId,List<ProviderAvailability> providerAvailabilities,
			Map<Integer,Integer>  serviceAppmtdurations) throws CacheException {

		for (ProviderAvailability providerAvailability : providerAvailabilities) {
			if(providerAvailability==null){
				return;
			}
			String booked;
			String serviceSchedules;
			try {
				booked = objectMapper.writeValueAsString(providerAvailability.getBooked());
				serviceSchedules = objectMapper.writeValueAsString(providerAvailability.getServiceSchedule());
			} catch (JsonProcessingException e) {
				throw new CacheException(ErrorStatusType.INTERNALSERVERERROR, "Exception while converting object to json string");
			}

			ProviderScheduleCache providerScheduleCache = new ProviderScheduleCache();
			providerScheduleCache.setAccountId(providerAvailability.getAccountId());
			providerScheduleCache.setBookedAppointments(booked);
			providerScheduleCache.setProviderScheduleStatus(true);
			providerScheduleCache.setDateOfAvailability(asDate(providerAvailability.getDate()));
			providerScheduleCache.setProviderId(providerAvailability.getProviderId());
			providerScheduleCache.setServiceSchedules(serviceSchedules);

			List<ProviderServiceScheduleCache> providerServiceScheduleCaches = new ArrayList<ProviderServiceScheduleCache>();
			/*
			 * save available schedule for all services assigned to provider into provider service schedule table 
			 */
			for (Entry<Integer, Schedule> serviceScheduleEntry : providerAvailability.getServiceAvailableSchedule().entrySet()) {
				/*boolean available  = isAvailableSlots(serviceScheduleEntry.getValue(),
						providerAvailability.getDate(),providerAvailability.getDate(),serviceAppmtdurations.get(serviceScheduleEntry.getKey()));
				 */
				ProviderServiceScheduleCache providerServiceScheduleCache  = new ProviderServiceScheduleCache();
				providerServiceScheduleCache.setAvailabileSchedule(serviceScheduleEntry.getValue().getTimeSlot());
				providerServiceScheduleCache.setAvailable(serviceScheduleEntry.getValue().isAvailable());
				providerServiceScheduleCache.setDateOfAvailability(asDate(providerAvailability.getDate()));
				providerServiceScheduleCache.setProviderId(providerAvailability.getProviderId());
				providerServiceScheduleCache.setServiceId(serviceScheduleEntry.getKey());
				providerServiceScheduleCache.setServiceScheduleStatus(ScheduleStatus.valid);
				providerServiceScheduleCaches.add(providerServiceScheduleCache);
			}
			this.saveProviderSchedulesIntoCache(providerScheduleCache,providerServiceScheduleCaches);
		}
	}
	/**
	 * Check for any available slots of timespec in particular duration (minutes)  within a date range
	 * @param timespecStr timespec 
	 * @param startDate {@link LocalDate}
	 * @param endDate {@link LocalDate}
	 * @param durationMins duration in minutes
	 * @return true if slots are available 
	 * @throws CacheException {@link CacheException}
	 */
	private boolean isAvailableSlots(String timespecStr,
			LocalDate startDate, LocalDate endDate, Integer durationMins) throws CacheException {
		try {
			TimeSpec timespec = TimeSpecFactory.getInstance(timespecStr);
			List<String> slots = timespec.availableTimeSlots(startDate, endDate, durationMins);
			if(slots!=null && slots.size()>0){
				for (String string : slots) {
					System.out.println("Slot:"+string);
				}
				return true;
			}
		} catch (TimeSpecException e) {
			logger.error(new NVLogFormatter("Exception in checking for available slots", e));
			throw new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in checking for available slots");
		}
		return false;
	}

	/**
	 * Get available schedules for all services of provider
	 * @param providerId provider id
	 * @param accountId account id
	 * @param businessSchedule business schedule
	 * @param providerServiceSchedules provider's services and its schedule
	 * @param vacationStr vacations array
	 * @param holidayStr holidays array
	 * @param days minimum appointment time of account
	 * @return list of {@link ProviderAvailability}
	 * @throws PersistenceException {@link PersistenceException}
	 * @throws CacheException {@link CacheException}
	 */
	private List<ProviderAvailability> getProviderAvailability(int providerId, int accountId,String businessSchedule,
			List<ServiceSchedule> providerServiceSchedules,String[] vacationStr, String[] holidayStr,int days,Map<Integer,Integer>  serviceAppmtdurations)
					throws  CacheException, PersistenceException {
		List<ProviderAvailability> providerAvailabilities = new ArrayList<ProviderAvailability>();
		Map<LocalDate,String> providerAvail = new HashMap<LocalDate,String>();
		Map<Integer, Map<LocalDate,String>> schedule = new HashMap<Integer, Map<LocalDate,String>>(); // Map<scheduleid,Map<scheduledate,timespec>>
		List<Schedule> schedules = readDao.executeQuery(Schedule.class,get_schedules_of_provider,providerId);
		for (Schedule scheduleOfProvider : schedules) {
			String timeSlot = scheduleOfProvider.getTimeSlot();
			int scheduleId = scheduleOfProvider.getId();
			String[] timeSpecs = timeSlot.split("&");

			/* Add schedule into schedule map with schedule id as key and map <date and timespec> as value*/
			Map<LocalDate,String> timespecMap = slice(timeSpecs,days);
			if(timespecMap.isEmpty()){
				System.out.println("TIMESPEC EMPTY...");
			}else{
				for (Entry<LocalDate, String> t : timespecMap.entrySet()) {
					System.out.println("========scheduleId:"+scheduleId+" date:"+t.getKey()+" timespec:"+t.getValue());
				}
			}
			TimeSpecOperations.mergeMapByDate(providerAvail,timespecMap);
			schedule.put(scheduleId, timespecMap);
		}

		System.out.println("****************");
		System.out.println("vacationStr:"+vacationStr);
		System.out.println("holidayStr:"+holidayStr);

		Map<LocalDate,String> vacationMap = slice(vacationStr,days);//Vacation slots
		Map<LocalDate,String> holidayMap = slice(holidayStr,days);//Holiday slots
		System.out.println("vacationMap slices empty?:"+vacationMap.isEmpty());
		System.out.println("holiday slices empty?:"+holidayMap.isEmpty());
		System.out.println("****************");
		/*
		 * Is there any Empty/null Schedule For Provider means buisiness schedule is assigned to him.
		 * So slice buisiness schedule into 31 days and add it into schedule map with schedule id = 0  
		 */

		if(hasBusinessSchedule(providerServiceSchedules, providerId)){
			if(businessSchedule!=null && !businessSchedule.isEmpty() && !businessSchedule.equalsIgnoreCase("null")){
				String[] timeSpecs = businessSchedule.split("&");
				/* Add schedule into schedule map with schedule id as key and map <date and timespec> as value*/
				Map<LocalDate,String> timespecMap = slice(timeSpecs,days);
				TimeSpecOperations.mergeMapByDate(providerAvail,timespecMap);
				schedule.put(0, timespecMap);
			}

		}

		Map<Integer,List<Integer>> scheduleServices = getScheduleServices(providerServiceSchedules);
		providerAvailabilities = getProviderAvailability(providerId,accountId,providerAvail,scheduleServices,schedule,
				vacationMap,holidayMap,serviceAppmtdurations,days);
		return providerAvailabilities;
	}

	/**
	 * Get available schedules  of given provider. Exclude already taken appointments, vacations and holidays from provider schedules
	 * @param providerId provider id
	 * @param accountId account id
	 * @param providerAvailabilityMap provider availability map with date as key and available timespec as value
	 * @param scheduleServices schedule and its services
	 * @param schedule schedule map with schedule id as key and schedule slices with date as value 
	 * @param vacationMap vacation map with date as key and vacation timespec as value
	 * @param holidayMap holiday map with date as key and holiday timespec as value
	 * @return list of {@link ProviderAvailability}
	 * @throws CacheException {@link CacheException}
	 */
	private List<ProviderAvailability> getProviderAvailability(int providerId,int accountId, Map<LocalDate,String> providerAvailabilityMap,
			Map<Integer,List<Integer>> scheduleServices, Map<Integer, Map<LocalDate,String>> schedule, Map<LocalDate,String> vacationMap,
			Map<LocalDate,String> holidayMap,Map<Integer,Integer>  serviceAppmtdurations,int days) throws CacheException{
		TimeRule timeRule = new TimeRule();
		List<ProviderAvailability> providerAvailabilities = new ArrayList<ProviderAvailability>();
		TimeRangeList ranges;
		for (Entry<LocalDate, String> providerAvail : providerAvailabilityMap.entrySet()) {
			//String providerAvailabilityStr = providerAvail.getValue();
			String vacationStr ="";
			String holidayStr = "";
			List<TimeRange> vacationTimeRanges =null;
			List<TimeRange> holidayTimeRanges =null;
			List<TimeRange> scheduleTimeRanges =null;

			/*  vacation time ranges*/ 
			if(vacationMap.get(providerAvail.getKey())!=null){
				vacationStr = vacationMap.get(providerAvail.getKey());
				try {
					ranges = timeRule.evaluate(vacationStr);
					vacationTimeRanges = ranges.getTimeRanges();
				} catch (TimeSpecException e) {
					logger.error(new NVLogFormatter("Exception in vacation time ranges calculation", e));
					throw new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in vacation time ranges calculation");
				}

			}

			/*   holiday time ranges*/ 
			if(holidayMap.get(providerAvail.getKey())!=null){
				holidayStr = holidayMap.get(providerAvail.getKey());
				try {
					ranges = timeRule.evaluate(holidayStr);
					holidayTimeRanges = ranges.getTimeRanges();
				} catch (TimeSpecException e) {
					logger.error(new NVLogFormatter("Exception in holiday time ranges calculation", e));
					throw new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in holiday time ranges calculation");
				}

			}
			System.out.println(providerAvail.getKey()+ "vacation: "+vacationStr+" holiday: "+holidayStr);
			ProviderAvailability providerAvailability = new ProviderAvailability(providerId, accountId, providerAvail.getKey(), 
					new HashMap<Integer,Schedule>(),null,new WorkMap(new ArrayList<BookedAppointment>(),holidayTimeRanges,vacationTimeRanges),
					new HashMap<Integer,String>());

			/* Subtract vacation,holidays and appointments from schedule */
			for (Entry<Integer, Map<LocalDate, String>> schedulemap : schedule.entrySet()) {
				Map<LocalDate, String> scheduleSlices = schedulemap.getValue();


				if(scheduleSlices.get(providerAvail.getKey())!=null){
					String schedulesliceStr = scheduleSlices.get(providerAvail.getKey());
					TimeSpec timespec ;
					try {
						if(vacationStr!=null && !vacationStr.isEmpty()&&schedulesliceStr!=null){
							timespec = TimeSpecFactory.getInstance(schedulesliceStr);
							timespec=timespec.subtract(vacationStr);
							if(timespec==null || timespec.asStringArray()==null || timespec.asStringArray().length==0){
								schedulesliceStr = null;
							}else{
								schedulesliceStr = timespec.asStringArray()[0];
								System.out.println("After vacationStrsubtraction:"+schedulesliceStr);
							}

						}
						if(holidayStr!=null && !holidayStr.isEmpty()&&schedulesliceStr!=null){
							timespec = TimeSpecFactory.getInstance(schedulesliceStr);
							timespec=timespec.subtract(holidayStr);
							if(timespec==null || timespec.asStringArray()==null || timespec.asStringArray().length==0){
								schedulesliceStr = null;
							}else{
								schedulesliceStr = timespec.asStringArray()[0];
								System.out.println("After holidaysubtraction:"+schedulesliceStr);
							}
						}
					} catch (TimeSpecException e) {
						logger.error(new NVLogFormatter("Exception in subtraction of vacation or holiday or appointment from schedule", e));
						throw new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in subtraction of vacation or holiday or appointment from schedule");
					}
					/* To keep all services and its available schedules of provider in the same date
					And also keep service and its actual schedule */ 
					if(scheduleServices.get(schedulemap.getKey())!=null){
						for (Integer serviceId : scheduleServices.get(schedulemap.getKey())) {
							if(schedulesliceStr!=null){
								boolean isAvailable = isAvailableSlots(schedulesliceStr, providerAvail.getKey(), providerAvail.getKey(), days);
								try {
									ranges = timeRule.evaluate(schedulesliceStr);
									scheduleTimeRanges = ranges.getTimeRanges();
									schedulesliceStr = objectMapper.writeValueAsString(scheduleTimeRanges);
								} catch (TimeSpecException | JsonProcessingException e) {
									logger.error(new NVLogFormatter("Exception in calculating timeranges of provider availability for a service", e));
									throw new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in calculating timeranges of provider availability for a service");
								}
								Schedule scheduleWithStatus  = new Schedule(schedulesliceStr,isAvailable);
								providerAvailability.getServiceAvailableSchedule().put(serviceId, scheduleWithStatus);
							}
							providerAvailability.getServiceSchedule().put(serviceId, scheduleSlices.get(providerAvail.getKey()));
						}
					}
				}
			}
			providerAvailabilities.add(providerAvailability);
		}
		Collections.sort(providerAvailabilities,new ProviderAvailabilityComparator());//Sort provider available schedules with date
		return providerAvailabilities;
	}

	/**
	 * Slice timespecs for given number of days
	 * @param timespecs timespec array
	 * @param days number of days
	 * @return map of date and sliced timespec for that day
	 * @throws PersistenceException {@link PersistenceException}
	 * @throws CacheException {@link CacheException} 
	 */
	private Map<LocalDate,String> slice(String[] timespecs,int days) throws CacheException{

		Map<LocalDate,String> slices = new HashMap<LocalDate, String>();
		if(timespecs==null){
			System.out.println("TIMESPECS NULL---");	
			return slices;
		}
		for (int i=0;i<timespecs.length;i++){
			System.out.println("INSLICE---"+timespecs[i]);	
		}
		/* Slice provider schedule into number of  days and keep in map<date,timespec> */
		TimeSpec timespec;
		try {
			timespec = TimeSpecFactory.getInstance(timespecs);
			LocalDate today = LocalDate.now();
			LocalDate enddate = today.plus(days-1, ChronoUnit.DAYS);
			System.out.println("curdate " + today + " date after "+days+" days : "+ enddate);

			slices = timespec.slice(enddate, enddate);
			return slices;
		} catch (TimeSpecException e) {
			throw new CacheException(ErrorStatusType.INTERNALSERVERERROR, "Exception in Timespec slicing");
		}
	}

	/**
	 * Check for any business schedule assigned to given provider 
	 * @param serviceSchedules list of {@link ServiceSchedule}
	 * @param providerId provider id
	 * @return true if there is any business schedule assigned to given provider
	 */
	private boolean hasBusinessSchedule(List<ServiceSchedule> serviceSchedules, int providerId) {
		for (ServiceSchedule serviceSchedule : serviceSchedules) {
			if(serviceSchedule.getProviderId()==providerId){
				if(serviceSchedule.getScheduleId()==0){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get all schedules and its assigned services 
	 * @param serviceSchedules list of {@link ServiceSchedule} 
	 * @return map with schedule id as key and service ids as value
	 */
	private Map<Integer,List<Integer>> getScheduleServices(List<ServiceSchedule> serviceSchedules) {
		Map<Integer,List<Integer>> sceduleServices = new HashMap<Integer,List<Integer>>();
		Map<Integer,Integer> sceduleService = new HashMap<Integer,Integer>();
		List<Integer> scheduleIds= new ArrayList<Integer>();
		for (ServiceSchedule serviceSchedule : serviceSchedules) {
			sceduleService.put(serviceSchedule.getScheduleId(), serviceSchedule.getServiceId());
			scheduleIds.add(serviceSchedule.getScheduleId());
		}
		for (Integer scheduleId : scheduleIds) {
			//to create map with schedule id as key and list of services as its value
			List<Integer> services = new ArrayList<Integer>();
			for (Entry<Integer, Integer> scheduleService : sceduleService.entrySet()) { //scheduleid is key and serviceid is value.
				if(scheduleService.getKey().intValue() == scheduleId.intValue()){
					services.add(scheduleService.getValue());
				}
			}
			sceduleServices.put(scheduleId, services);
		}

		return sceduleServices;
	}
	/**
	 * Convert list of timespecs with seperator to string array
	 * @param timeSpecWithSeperator list of timespecs with seperator
	 * @return timespec array
	 */
	private String[] timespecsWithSeperatorToArray(List<String> timeSpecWithSeperator){
		String[] timespecsArr = null ;
		List<String> timespecs = new ArrayList<String>();
		for (String timeSpecWithSep : timeSpecWithSeperator) {
			String[] strArr = timeSpecWithSep.split("&");

			for (String timespecStr : strArr) {
				System.out.println(timespecStr);
				timespecs.add(timespecStr);
			}
		}
		timespecsArr = timespecs.toArray(new String[0]);
		return timespecsArr;
	}

	/**
	 * Convert {@link LocalDate} into {@link Date}
	 * @param localDate {@link LocalDate}
	 * @return date
	 */
	private  Date asDate(LocalDate localDate) {
		if(localDate!=null){
			return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}

	private Map<Integer,Integer> getAppmtDurationOfServices(int accountId) throws PersistenceException{
		//Query query = readDao.executeQuery(get_appmnt_duration_services);
		//		query.setParameter("param1", accountId);
		List<Service> services = readDao.executeQuery(Service.class,get_appmnt_duration_services, accountId);
		Map<Integer,Integer> serviceAppmtDuration = new HashMap<Integer,Integer>();
		for (Service service : services) {
			serviceAppmtDuration.put(service.getId(), service.getAppmtDuration());
		}
		return serviceAppmtDuration;
	}


	private void saveProviderSchedulesIntoCache(ProviderScheduleCache providerScheduleCache,
			List<ProviderServiceScheduleCache> providerServiceScheduleCaches) throws CacheException{
		cacheDao.save(providerScheduleCache);
		for (ProviderServiceScheduleCache providerServiceScheduleCache : providerServiceScheduleCaches) {
			cacheDao.save(providerServiceScheduleCache);
		}
	}
	public void setReadDao(ReadDao readDao) {
		this.readDao = readDao;
	}

	public void setCacheDao(CacheDao cacheDao) {
		this.cacheDao = cacheDao;
	}

	public void setObjectMapper(JSONMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}