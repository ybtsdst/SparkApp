package com.hqkang.SparkApp.geom;

import scala.Tuple2;

public class MBRRDDKey extends Tuple2<Integer, String>{


	public MBRRDDKey(int _1, String _2) {
		super(_1, _2);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof MBRRDDKey))
			return false;
		MBRRDDKey key = (MBRRDDKey) o;
		return this._1.equals(key._1)
				&& this._2.equals(key._2);
	}
	
	@Override
	public int hashCode() {
		return this._1 +_2.hashCode();
		
	}


}
