package ewing.common;

import ewing.application.ResultMessage;
import ewing.application.query.Page;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.entity.Dictionary;
import ewing.security.AuthorityCodes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据字典控制器。
 **/
@RestController
@RequestMapping("/dictionary")
@Api(tags = "dictionary", description = "数据字典")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @ApiOperation("新增字典项")
    @PostMapping("/addDictionary")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.DICTIONARY_ADD + "')")
    public ResultMessage<?> addDictionary(@RequestBody Dictionary dictionary) {
        dictionaryService.addDictionary(dictionary);
        return new ResultMessage<>();
    }

    @ApiOperation("更新字典项")
    @PostMapping("/updateDictionary")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.DICTIONARY_UPDATE + "')")
    public ResultMessage<?> updateDictionary(@RequestBody Dictionary dictionary) {
        dictionaryService.updateDictionary(dictionary);
        return new ResultMessage<>();
    }

    @ApiOperation("删除字典项")
    @PostMapping("/deleteDictionary")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.DICTIONARY_DELETE + "')")
    public ResultMessage<?> deleteDictionary(Long dictionaryId) {
        dictionaryService.deleteDictionary(dictionaryId);
        return new ResultMessage<>();
    }

    @ApiOperation("查询字典项及子项")
    @PostMapping("/findWithSubDictionary")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.DICTIONARY_MANAGE + "')")
    public ResultMessage<Page<Dictionary>> findWithSubDictionary(
            @RequestBody FindDictionaryParam findDictionaryParam) {
        return new ResultMessage<>(dictionaryService
                .findWithSubDictionary(findDictionaryParam));
    }

    @ApiOperation("根据根字典值查询字典树")
    @PostMapping("/findDictionaryTrees")
    public ResultMessage<List<DictionaryNode>> findDictionaryTrees(
            @RequestBody String[] rootValues) {
        return new ResultMessage<>(dictionaryService.findDictionaryTrees(rootValues));
    }

}
