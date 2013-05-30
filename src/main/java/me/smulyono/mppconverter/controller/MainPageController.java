package me.smulyono.mppconverter.controller;

import java.io.IOException;

import me.smulyono.mppconverter.model.FileUploadForm;
import me.smulyono.mppconverter.model.Project;
import me.smulyono.mppconverter.service.ConverterService;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainPageController {
	static Logger logger = LoggerFactory.getLogger(MainPageController.class); 
	
	@Autowired
	ConverterService mppconverter;
	
	@RequestMapping(value="/")
	public String IndexPage(Model model) {
		fillDefault(model);
		return "index";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	@ResponseBody
	public Project saveFile(
				@ModelAttribute("uploadForm") FileUploadForm uploadform,
				Model model){
		fillDefault(model);
		model.addAttribute("uploaded_file", uploadform.getFile().getOriginalFilename());
		Project result = new Project();
		try {
			ProjectFile project = mppconverter.ConvertFile(uploadform.getFile(), ConverterService.MPP_TYPE);
			model.addAttribute("project_info", project.getProjectHeader().getProjectTitle());
			result = new Project(project);
		} catch (MPXJException ex){
			logger.error("MPXJ Error :: " + ex.getMessage());
		} catch (IOException ex){
			logger.error("IOException Error :: " + ex.getMessage());
		}
		
		return result;
	}
	
	private void fillDefault(Model model){
		model.addAttribute("title", "MPP/X Converter in Heroku");
		model.addAttribute("subtitle", "MPP/X Converter to JSON");
	}
}