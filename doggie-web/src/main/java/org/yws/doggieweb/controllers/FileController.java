package org.yws.doggieweb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yws.doggieweb.models.CommonResponse;
import org.yws.doggieweb.models.FileEntity;
import org.yws.doggieweb.models.FileType;
import org.yws.doggieweb.service.FileService;

import java.util.List;

/**
 * Created by ywszjut on 15/7/25.
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "get")
    @ResponseBody
    public FileEntity get(Long id) {
        return fileService.findOne(id);
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public List<FileEntity> list(Long parent, String[] properties, String[] directions) {
        if (properties != null && directions != null && properties.length == directions.length) {
            String[][] orderProperties = new String[properties.length][2];
            for (int i = 0; i < properties.length; i++)
                orderProperties[i] = new String[]{properties[i], directions[i]};
            return fileService.listFilesByParent(parent, orderProperties);
        } else {
            return fileService.listFilesByParent(parent);
        }
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public CommonResponse save(String name, Long parent, Integer fileType) {
        Long id = null;
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(name);
            fileEntity.setParent(new FileEntity(parent));
            fileEntity.setFileType(FileType.fromInteger(fileType));
            id = fileService.save(fileEntity);
        } catch (Exception e) {
            return new CommonResponse(CommonResponse.FAILED, "保存失败", e.getMessage());
        }
        return new CommonResponse(CommonResponse.SUCCESS, "保存成功", id);
    }

    @RequestMapping(value = "rename")
    @ResponseBody
    public CommonResponse rename(Long id, String name) {
        try {
            FileEntity file = fileService.findOne(id);
            if(file==null){
                return CommonResponse.FAILED("重命名失败，ID不存在");
            }else if (StringUtils.isEmpty(name)){
                return CommonResponse.FAILED("新名字不能为空");
            }
            file.setName(name);
            fileService.save(file);
        } catch (Exception e) {
            return CommonResponse.FAILED("重命名失败" + e.getMessage());
        }
        return CommonResponse.SUCCESS("重命名成功");
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public CommonResponse delete(Long id) {
        try {
            if (fileService.findOne(id).getParent() != null) {
                fileService.delete(id);
            } else {
                return CommonResponse.FAILED("根节点能删除");
            }

        } catch (Exception e) {
            return CommonResponse.FAILED("删除失败" + e.getMessage());
        }
        return CommonResponse.SUCCESS("删除成功");
    }
}
