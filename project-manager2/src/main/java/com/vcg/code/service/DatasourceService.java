package com.vcg.code.service;

import com.vcg.common.base.BaseService;

import java.util.List;

import com.vcg.code.model.Datasource;
import com.vcg.code.model.query.DatasourceExample;

public interface DatasourceService extends BaseService<Datasource, DatasourceExample, Integer> {

	public List<Datasource> listDataSource();
}