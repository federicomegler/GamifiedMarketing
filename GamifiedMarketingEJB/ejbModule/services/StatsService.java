package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import exceptions.StatisticsException;

@Stateless
public class StatsService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public TreeMap<Date, Integer> getLogsLast7Days() throws StatisticsException {
		TreeMap<Date, Integer> logs = new TreeMap<Date, Integer>();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		
		
		for(int i=0; i<7; ++i) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR,-i);
			cal.getTime();
			List<Long> count = null;
			try {
				count = em.createQuery("SELECT COUNT(l) FROM Log l WHERE l.date = :date", Long.class).setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
			}
			catch(PersistenceException e) {
				throw new StatisticsException("Unable to get logs!");
			}
			System.out.println(cal.getTime());
			System.out.println(count);
			logs.put(cal.getTime(), count.get(0).intValue());
		}
		
		return logs;
	}
	
	public Double getAvgAge() throws StatisticsException {
		List<Double> avg = null;
		try {
			avg = em.createQuery("SELECT avg(a.age) FROM StatisticalAnswer a", Double.class).getResultList();
		}
		catch(PersistenceException e) {
			throw new StatisticsException("Unable to get average age");
		}
		return avg.get(0);
	}
	
	
	public Double getAvgEXP() throws StatisticsException {
		List<Double> avg = null;
		try {
			avg = em.createQuery("SELECT avg(a.expertise_level) FROM StatisticalAnswer a", Double.class).getResultList();
		}
		catch(PersistenceException e) {
			throw new StatisticsException("Unable to get avg expertise level");
		}
		return avg.get(0);
	}
	
	public Long getTotalLogs() throws StatisticsException {
		List<Long> tot = null;
		try {
			tot = em.createQuery("SELECT COUNT(l) FROM Log l", Long.class).getResultList();
		}
		catch(PersistenceException e) {
			throw new StatisticsException("Unable to get total logs");
		}
		return tot.get(0);
	}
	
	public Map<String, Long> getGenderDistribution() throws StatisticsException {
		Map<String, Long> distribution = new HashMap<String, Long>();
		Long male = null;
		Long female = null;
		Long none = null;
		try {
			male = em.createQuery("SELECT COUNT(a) FROM StatisticalAnswer a WHERE a.gender = 'M'", Long.class).getResultList().get(0);
			female = em.createQuery("SELECT COUNT(a) FROM StatisticalAnswer a WHERE a.gender = 'F'", Long.class).getResultList().get(0);
			none = em.createQuery("SELECT COUNT(a) FROM StatisticalAnswer a WHERE a.gender = null", Long.class).getResultList().get(0);
		}
		catch(PersistenceException e) {
			throw new StatisticsException("Unable to get gender statistics");
		}
		distribution.put("Male", male);
		distribution.put("Female", female);
		distribution.put("None", none);
		return distribution;
	}
	
	public TreeMap<Date, List<Long>> getSubmitStats() throws StatisticsException {
		TreeMap<Date, List<Long>> logs = new TreeMap<Date, List<Long>>();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		
		
		for(int i=0; i<7; ++i) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR,-i);
			cal.getTime();
			List<Long> count = new ArrayList<Long>();
			try {
				count.add(em.createQuery("SELECT COUNT(DISTINCT(a.user_answer)) FROM Answer a INNER JOIN a.question q INNER JOIN q.product p WHERE p.date = :date", Long.class)
						.setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList().get(0));
				count.add(em.createQuery("SELECT COUNT(DISTINCT(l.user_log)) FROM Log l INNER JOIN l.product_log p WHERE p.date = :date AND l.user_log NOT IN (SELECT a.user_answer FROM Answer a INNER JOIN a.question q WHERE q.product = p)", Long.class)
						.setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList().get(0));
			}
			catch(PersistenceException e) {
				throw new StatisticsException("Unable to get submit statistics");
			}
			System.out.println(cal.getTime());
			System.out.println(count);
			logs.put(cal.getTime(), count);
		}
		
		return logs;
	}
}
