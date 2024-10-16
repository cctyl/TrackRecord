package cn.tyl.gps_demo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.tyl.gps_demo.entity.GPSData;

@Dao
public interface GpsDataDao {

    @Insert
    List<Long> insert(List<GPSData> dataList);

    @Insert
    Long insert(GPSData  GPSData);

    //我也没写条件，那么他应该默认就是根据id进行删除的把
    @Delete
    int delete(GPSData ...GPSDatas);

    /**
     * delete 方法的返回值要么是int，要么是void，不能是其他
     * @return
     */
    @Query(" delete from gps_data ")
    int deleteAll();

    @Query(" select count(1) from gps_data ")
    int count();

    @Query(" delete from gps_data where id = :id  ")
    int deleteById(Integer id);


    /**
     * 这个query注解里面，GPSData as 是有提示的，没有就写表名
     * @return
     */
    @Query(" select * from gps_data ")
    List<GPSData> findAll();

    @Query(" select * from gps_data order by time desc limit 0,1 ")
    GPSData findFirst();


}
