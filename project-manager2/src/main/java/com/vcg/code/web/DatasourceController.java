package com.vcg.code.web;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vcg.code.gennerator.mybatis.GeneratorSqlmap;
import com.vcg.code.model.Datasource;
import com.vcg.code.service.DatasourceService;
import com.vcg.common.utils.FileUtil;

@Controller
@RequestMapping("/db")
public class DatasourceController {

	private final String REDIRECT_LIST_PAGE = "redirect:listDatasource";

	@Autowired
	DatasourceService datasourceService;

	@RequestMapping("/listDatasource")
	public String listDatasource(HttpServletRequest request) {
		List<Datasource> datasources = datasourceService.listDataSource();
		request.setAttribute("datasources", datasources);
		return "code/listDB";
	}

	@RequestMapping("/addOrUpdate")
	public String addDB(Datasource datasource) {
		if (datasource.getId() != null) {
			datasourceService.updateByPrimaryKeySelective(datasource);
		} else {
			datasourceService.insertSelective(datasource);
		}
		return REDIRECT_LIST_PAGE;
	}

	@RequestMapping("/delDB")
	public void delDB(Integer id) {
		datasourceService.deleteByPrimaryKey(id);
	}

	@RequestMapping("/getById")
	@ResponseBody
	public Datasource getById(Integer id) {
		Datasource datasource = datasourceService.selectByPrimaryKey(id);
		datasource.setPassword(null);
		return datasource;
	}

	@RequestMapping("/gennerator")
	public void gennerator(HttpServletResponse response, Integer id, String project, String packagePath, String tbls)
			throws Exception {
		GeneratorSqlmap gen = new GeneratorSqlmap();
		Datasource datasource = datasourceService.selectByPrimaryKey(id);
		List<String> tables = new ArrayList<>();
		if (StringUtils.isNotBlank(tbls)) {
			for (String table : tbls.split(",")) {
				tables.add(table.trim());
			}
		}
		FileInputStream in = gen.generator(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPassword(),
				datasource.getDriver(), project, packagePath, tables);
		FileUtil.down(response, in, project + "-parent.zip");
	}

	@RequestMapping("/genneratorWeb")
	public void genneratorWeb(HttpServletResponse response, Integer id, String project, String packagePath, String tbls,
			String containerType) throws Exception {
		GeneratorSqlmap gen = new GeneratorSqlmap();
		Datasource datasource = datasourceService.selectByPrimaryKey(id);
		List<String> tables = new ArrayList<>();
		if (StringUtils.isNotBlank(tbls)) {
			for (String table : tbls.split(",")) {
				tables.add(table.trim());
			}
		}
		FileInputStream in = gen.generatorWeb(datasource.getJdbcUrl(), datasource.getUsername(),
				datasource.getPassword(), datasource.getDriver(), project, packagePath, tables, containerType);
		FileUtil.down(response, in, project + ".zip");
	}
	
	@RequestMapping("/genneratorBoot")
	public void genneratorRoot(HttpServletResponse response, Integer id, String project, String packagePath, String tbls,
			String containerType,boolean isGenerator) throws Exception {
		GeneratorSqlmap gen = new GeneratorSqlmap();
		Datasource datasource = datasourceService.selectByPrimaryKey(id);
		List<String> tables = new ArrayList<>();
		if (StringUtils.isNotBlank(tbls)) {
			for (String table : tbls.split(",")) {
				tables.add(table.trim());
			}
		}
		FileInputStream in = gen.genneratorBoot(datasource.getJdbcUrl(), datasource.getUsername(),
				datasource.getPassword(), datasource.getDriver(), project, packagePath, tables,isGenerator);
		FileUtil.down(response, in, project + ".zip");
	}
	
	@RequestMapping("/genneratorCloud")
	public void genneratorCloud(HttpServletResponse response, Integer id, String project, String packagePath, String tbls,
			String containerType,boolean isGenerator) throws Exception {
		GeneratorSqlmap gen = new GeneratorSqlmap();
		Datasource datasource = datasourceService.selectByPrimaryKey(id);
		List<String> tables = new ArrayList<>();
		if (StringUtils.isNotBlank(tbls)) {
			for (String table : tbls.split(",")) {
				tables.add(table.trim());
			}
		}
		FileInputStream in = gen.genneratorCloud(datasource.getJdbcUrl(), datasource.getUsername(),
				datasource.getPassword(), datasource.getDriver(), project, packagePath, tables,isGenerator);
		FileUtil.down(response, in, project + ".zip");
	}

}
