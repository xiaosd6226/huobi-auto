package io.yule.huobiauto.dao;

import io.yule.huobiauto.entity.EnumerationType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chensijiang on 2018/4/15 下午8:49.
 */
public interface EnumerationTypeDao extends PagingAndSortingRepository<EnumerationType, String> {
}
