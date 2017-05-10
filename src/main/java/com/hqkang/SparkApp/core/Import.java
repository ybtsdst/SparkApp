package com.hqkang.SparkApp.core;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import com.hqkang.SparkApp.cli.SubmitParser;
import com.hqkang.SparkApp.geom.MBR;
import com.hqkang.SparkApp.geom.MBRList;

import scala.Tuple2;

public class Import {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Create a Java Spark Context
		
		
		SubmitParser parser = new SubmitParser(args);
		//String filePath  = "000/Trajectory";
		//ResourceBundle rb = ResourceBundle.getBundle("Config");
		String filePath = parser.getIPath();
		int k = parser.getSegNum();
		int part = parser.getPart();
		Builder  blder = 
		 SparkSession.builder().appName("ImportSeg");
		
		
		if(parser.getDebug()) {
			blder.master("local");
		} 
		SparkSession spark = blder.getOrCreate();
		spark.conf().set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		spark.conf().set("spark.kryo.registrator", "MyRegistrator");
		JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
		//sc.hadoopConfiguration().set("fs.s3n.awsAccessKeyId", parser.getAccessID());
		//sc.hadoopConfiguration().set("fs.s3n.awsSecretAccessKey", parser.getSecretKey()); // can contain "/"

		
		
		//List<File> file = 	Helper.ReadAllFile(filePath);
		//Iterator<File> ite = file.iterator();
		
		//String fileName = ite.next().getPath();
		JavaPairRDD<String, MBRList> mbrRDD =  CommonHelper.importFromFile(filePath, sc, k, part);

		try(Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost", "neo4j", "25519173")) {
			String query = "call spatial.addWKTLayer('geom','wkt')";
			 try (PreparedStatement stmt = con.prepareStatement(query)) {

			        try (ResultSet rs = stmt.executeQuery()) {
			        	con.commit();
			        	con.close();
			            while (rs.next()) {
			            	
			                System.out.println(rs.getString(1));
			            }
			        }
			    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		JavaPairRDD<Tuple2<Integer, String>, MBR> databaseRDD = DBHelper.store2DB(mbrRDD).cache();

		databaseRDD.foreach(new VoidFunction<Tuple2<Tuple2<Integer,String>,MBR>>() {

			

			@Override
			public void call(Tuple2<Tuple2<Integer, String>, MBR> t) throws Exception {				// TODO Auto-generated method stub
				System.out.println(t._1+ "----" +t._2);
				
			}

		
				
			}); 
		//databaseRDD.count(); 
	
	
		sc.stop();
	
	}

	private static ArrayList<File> ReadAllFiles() {
		// TODO Auto-generated method stub
		return null;
	}
  

}