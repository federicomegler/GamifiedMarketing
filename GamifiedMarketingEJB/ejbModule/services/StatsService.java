package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

@Stateless
public class StatsService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public TreeMap<Date, Integer> getLogsLast7Days() {
		TreeMap<Date, Integer> logs = new TreeMap<Date, Integer>();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		
		
		for(int i=0; i<7; ++i) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR,-i);
			cal.getTime();
			List<Long> count = em.createQuery("SELECT COUNT(l) FROM Log l WHERE l.date = :date", Long.class).setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
			System.out.println(cal.getTime());
			System.out.println(count);
			logs.put(cal.getTime(), count.get(0).intValue());
		}
		
		return logs;
	}
	
	public Double getAvgAge() {
		List<Double> avg = em.createQuery("SELECT avg(a.age) FROM StatisticalAnswer a", Double.class).getResultList();
		return avg.get(0);
	}
	
	
	public Double getAvgEXP() {
		List<Double> avg = em.createQuery("SELECT avg(a.expertise_level) FROM StatisticalAnswer a", Double.class).getResultList();
		return avg.get(0);
	}
	
	public Long getTotalLogs() {
		List<Long> tot = em.createQuery("SELECT COUNT(l) FROM Log l", Long.class).getResultList();
		return tot.get(0);
	}
	
	public Map<String, Long> getGenderDistribution() {
		Map<String, Long> distribution = new HashMap<String, Long>();
		Long male = em.createQuery("SELECT COUNT(a) FROM StatisticalAnswer a WHERE a.gender = 'M'", Long.class).getResultList().get(0);
		Long female = em.createQuery("SELECT COUNT(a) FROM StatisticalAnswer a WHERE a.gender = 'F'", Long.class).getResultList().get(0);
		Long none = em.createQuery("SELECT COUNT(a) FROM StatisticalAnswer a WHERE a.gender = null", Long.class).getResultList().get(0);
		
		distribution.put("Male", male);
		distribution.put("Female", female);
		distribution.put("None", none);
		return distribution;
	}
	
	public TreeMap<Date, List<Long>> getSubmitStats() {
		TreeMap<Date, List<Long>> logs = new TreeMap<Date, List<Long>>();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		
		/*SELECT DISTINCT(a.user_answer) FROM Answer a JOIN Question q ON (a.question = q) WHERE q.product = :prod*/
		
		for(int i=0; i<7; ++i) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR,-i);
			cal.getTime();
			List<Long> count = new ArrayList<Long>();
			count.add(em.createQuery("SELECT COUNT(DISTINCT(a.user_answer)) FROM Answer a INNER JOIN a.question q INNER JOIN q.product p WHERE p.date = :date", Long.class)
					.setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList().get(0));
			count.add(em.createQuery("SELECT COUNT(DISTINCT(l.user_log)) FROM Log l INNER JOIN l.product_log p WHERE p.date = :date AND l.user_log NOT IN (SELECT a.user_answer FROM Answer a INNER JOIN a.question q WHERE q.product = p)", Long.class)
					.setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList().get(0));
			System.out.println(cal.getTime());
			System.out.println(count);
			logs.put(cal.getTime(), count);
		}
		
		return logs;
	}
}
