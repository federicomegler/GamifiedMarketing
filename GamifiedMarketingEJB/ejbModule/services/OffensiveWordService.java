package services;

import javax.ejb.Stateless;

@Stateless
public class OffensiveWordService {
	
	//this is the query: select word from gamifiedmarketingdb.offensiveword where "this is a string that contain a bad word" like concat("%", word, "%");
}
