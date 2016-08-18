package ${packagePath}.feignClient;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//@FeignClient(url="http://localhost:8001/") 直连
//调用时参数不能为null user为注册服务的名字 TODO 急需找对应的方法
@FeignClient("vcg-${project}")
public interface ${project}Client {

	//@RequestMapping(value="/accounts/list",method={RequestMethod.POST}, consumes = "application/json",produces = "application/json; charset=UTF-8")
	//public List<Account> list(Account accout);
}
