package com.konka.renting.landlord.house;


import com.konka.renting.landlord.house.data.MissionEnity;

import java.util.List;
import java.util.Map;

public interface HouseContract {
 public interface IMissionView{
	 /**
	  * 展示任务列表
	  * */
	 public void setDataAdapter(List<MissionEnity> list);

 }
 public interface IMissionData{
	 /**
	  * 获取任务数据
	  * */
	 public List<MissionEnity> getMissionData();
	 /**
	  * 保存到航空表
	  * */
	 public boolean saveData(Map<String, String> map);
 }
 public interface IMissonPresenter{
	 /**
	  * 获取任务数据
	  * */
	 public void getMissionData();
	 public void getMissionDataByvariable(String start, String end, String type, String isperform);
	 /**
	  * 保存数据到航空表
	  * */
	 public boolean saveData(Map<String, String> map);
	 
 }
}
