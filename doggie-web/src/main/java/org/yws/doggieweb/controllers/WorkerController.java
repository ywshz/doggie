package org.yws.doggieweb.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.yws.doggieweb.models.WorkerEntity;
import org.yws.doggieweb.service.JobService;

@Controller
@RequestMapping("/workers")
public class WorkerController {
	@Autowired
	private JobService jobService;

	@RequestMapping(value = "list")
	@ResponseBody
	public List<WorkerEntity> list() {
		return jobService.listWorkers();
	}

	@RequestMapping(value = "save")
	public String save(WorkerEntity entity,RedirectAttributesModelMap modelMap) {
		if(jobService.saveWorker(entity)){
			modelMap.addFlashAttribute("status", Boolean.TRUE);
		}else{
			modelMap.addFlashAttribute("status", Boolean.FALSE);
			modelMap.addFlashAttribute("message", "操作失败,可能有重复项");
		}
		return "redirect:/worker_manage";
	}

	@RequestMapping(value = "delete")
	public String delete(Long id,RedirectAttributesModelMap modelMap) {
		if(jobService.deleteWorker(id)){
			modelMap.addFlashAttribute("status", Boolean.TRUE);
		}else{
			modelMap.addFlashAttribute("status", Boolean.FALSE);
			modelMap.addFlashAttribute("message", "操作失败,ID不存在");
		}
		return "redirect:/worker_manage";
	}
}
