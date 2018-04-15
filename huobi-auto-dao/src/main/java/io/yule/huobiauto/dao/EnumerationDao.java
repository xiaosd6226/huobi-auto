package io.yule.huobiauto.dao;

import io.yule.huobiauto.entity.Enumeration;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chensijiang on 2018/4/15 下午8:48.
 */
public interface EnumerationDao extends PagingAndSortingRepository<Enumeration, String> {
}
