package com.balashenka.comrade.controller.v1;

import com.balashenka.comrade.controller.ApiPath;
import com.balashenka.comrade.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(ApiPath.API_V1_COMRADE_GROUPS_PATH)
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping(path = "import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void load(@RequestParam(value = "group-name") String groupName,
                     @NonNull @RequestParam(value = "file") MultipartFile tableData) {
        try {
            groupService.load(groupName, tableData.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @PostMapping(path = "{group-name}/export", produces = {"text/csv"})
    public ResponseEntity<Resource> export(@PathVariable(name = "group-name") String groupName) {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + groupName + ".csv");
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        var exported = groupService.export(groupName);

        return new ResponseEntity<>(new InputStreamResource(exported), headers, HttpStatus.OK);
    }

    @DeleteMapping(path = "{group-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "group-name") String groupName) {
        groupService.delete(groupName);
    }
}
