package ${packagePath}.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;




import ${packagePath}.model.${modelName};
import ${packagePath}.model.query.${modelName}Example;
import ${packagePath}.service.${modelName}Service;

//@Api(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/${lmodelName}")
public class ${modelName}Controller {

	@Autowired
	private ${modelName}Service ${lmodelName}Service;
	
	//@ApiOperation(value = "pageList", notes = "列表信息", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/pageList",method={RequestMethod.GET,RequestMethod.POST})
	public Object pageList(@RequestBody(required=false) ${modelName} ${lmodelName},@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue="10") int pageSize,HttpServletRequest request){
		
		${modelName}Example example=new ${modelName}Example();
		
		//TODO ${modelName} 与 example关联
		
		
		PageHelper.startPage(pageNum, pageSize);
		List<${modelName}> lst = this.${lmodelName}Service.selectByExample(example);
		return  new PageInfo<${modelName}>(lst);
	}
	
	
	@RequestMapping(value="/list",method={RequestMethod.GET,RequestMethod.POST})
	public Object list(@RequestBody(required=false) ${modelName} ${lmodelName}){
		
		${modelName}Example example=new ${modelName}Example();
		//TODO ${modelName} 与 example关联
		
		
		List<${modelName}> lst = this.${lmodelName}Service.selectByExample(example);
		return  lst;
	}
	
	//@ApiOperation(value = "添加用户", notes = "获取商品信息(用于数据同步)", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public Object create(@RequestBody ${modelName} ${lmodelName}){
		
		return this.${lmodelName}Service.insertSelective(${lmodelName});
		
	}
	//@ApiOperation(value = "update", notes = "获取商品信息(用于数据同步)", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public Object update(@RequestBody ${modelName} ${lmodelName}){
		
		return this.${lmodelName}Service.updateByPrimaryKeySelective(${lmodelName});
		
	}
	
	//@ApiOperation(value = "view", notes = "获取商品信息(用于数据同步)", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/view",method=RequestMethod.GET)
	public Object view(@RequestParam ${pkclass} id){
		
		return this.${lmodelName}Service.selectByPrimaryKey(id);
		
	}
	

	//@ApiOperation(value = "delete", notes = "获取商品信息(用于数据同步)", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public Object delete(@RequestBody Map<String,${pkclass}> ids){
		
		return this.${lmodelName}Service.deleteByPrimaryKey(ids.get("id"));
		
	}
	
}
