package services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

@Stateless
public class StatsService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Map<String, Integer> getLogsLast7Days() {
		Map<String, Integer> logs = new HashedMap<String, Integer>();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		
		
		for(int i=0; i<7; ++i) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR,-i);
			cal.getTime();
			List<Long> count = em.createQuery("SELECT COUNT(l) FROM Log l WHERE l.date = :date", Long.class).setParameter("date", cal.getTime()).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
			System.out.println(cal.getTime());
			System.out.println(count);
			logs.put(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()), count.get(0).intValue());
		}
		
		return logs;
	}
}
