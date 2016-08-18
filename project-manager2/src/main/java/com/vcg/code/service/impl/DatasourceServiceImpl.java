package com.vcg.code.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcg.common.base.BaseServiceImpl;
import com.vcg.code.model.Datasource;
import com.vcg.code.model.query.DatasourceExample;
import com.vcg.code.dao.DatasourceDao;
import com.vcg.code.service.DatasourceService;

@Service
public class DatasourceServiceImpl extends BaseServiceImpl<Datasource,DatasourceExample,Integer> implements DatasourceService {

	@Autowired
	private DatasourceDao datasourceDao;

	@Override
	public void setDao() {
		this.baseDao = datasourceDao;
	}

	@Override
	public List<Datasource> listDataSource() {
		return datasourceDao.selectByExample(new DatasourceExample());
	}

}