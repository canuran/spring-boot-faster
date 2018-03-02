package ewing.security;

import ewing.application.RequestUtils;
import ewing.application.ResultMessage;
import ewing.application.common.OkHttpUtils;
import ewing.application.exception.AppRunException;
import ewing.application.query.Page;
import ewing.entity.Authority;
import ewing.security.vo.AuthorityNode;
import ewing.security.vo.FindRoleParam;
import ewing.security.vo.RoleWithAuthority;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 安全控制器。
 **/
@RestController
@RequestMapping("/security")
@Api(tags = "security", description = "安全接口")
public class SecurityController {

    private static String prefix = "package " +
            SecurityController.class.getPackage().getName() +
            ";\n\n" +
            "/**\n" +
            " * 权限编码常量类，该类由接口生成。\n" +
            " */\n" +
            "public final class AuthorityCodes {";

    private static String suffix = "\n}";

    @Autowired
    private SecurityService securityService;

    @ApiOperation("获取当前登陆的用户信息")
    @GetMapping("/getCurrentUser")
    public ResultMessage<SecurityUser> getCurrentUser() {
        return new ResultMessage<>(RequestUtils.getCurrentUser());
    }

    @ApiOperation("新增权限")
    @PostMapping("/addAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.AUTHORITY_ADD + "')")
    public ResultMessage<?> addAuthority(@RequestBody Authority authority) {
        securityService.addAuthority(authority);
        return new ResultMessage<>();
    }

    @ApiOperation("更新权限")
    @PostMapping("/updateAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.AUTHORITY_UPDATE + "')")
    public ResultMessage<?> updateAuthority(@RequestBody Authority authority) {
        securityService.updateAuthority(authority);
        return new ResultMessage<>();
    }

    @ApiOperation("删除权限")
    @PostMapping("/deleteAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.AUTHORITY_DELETE + "')")
    public ResultMessage<?> deleteAuthority(Long authorityId) {
        securityService.deleteAuthority(authorityId);
        return new ResultMessage<>();
    }

    @ApiOperation("获取所有权限")
    @GetMapping("/getAllAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.AUTHORITY_MANAGE + "')")
    public ResultMessage<List<Authority>> getAllAuthority() {
        return new ResultMessage<>(securityService.getAllAuthority());
    }

    @ApiOperation("导出权限编码常量类")
    @GetMapping("/exportAuthorityCodes")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.AUTHORITY_MANAGE + "')")
    public void exportAuthorityCodes(HttpServletResponse response) {
        StringBuilder content = new StringBuilder(prefix);
        List<Authority> authorities = securityService.getAllAuthority();
        for (Authority authority : authorities) {
            content.append("\n    // ").append(authority.getName())
                    .append("\n    public static final String ")
                    .append(authority.getCode()).append(" = \"")
                    .append(authority.getCode()).append("\";");
        }
        content.append(suffix);
        // 导出文件输出流
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
        try {
            String fileName = OkHttpUtils.encodeUrl("AuthorityCodes.java");
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            response.getWriter().print(content.toString());
            response.getWriter().flush();
        } catch (IOException e) {
            throw new AppRunException("导出文件输出流错误！");
        }
    }

    @ApiOperation("获取所有权限树")
    @GetMapping("/getAuthorityTree")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.ROLE_MANAGE + "')")
    public ResultMessage<List<AuthorityNode>> getAuthorityTree() {
        return new ResultMessage<>(securityService.getAuthorityTree());
    }

    @ApiOperation("分页查找角色带权限")
    @PostMapping("/findRoleWithAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.ROLE_MANAGE + "')")
    public ResultMessage<Page<RoleWithAuthority>> findRoleWithAuthority(
            @RequestBody FindRoleParam findRoleParam) {
        return new ResultMessage<>(securityService.findRoleWithAuthority(findRoleParam));
    }

    @ApiOperation("新增角色及其权限关联")
    @PostMapping("/addRoleWithAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.ROLE_ADD + "')")
    public ResultMessage<?> addRoleWithAuthority(
            @RequestBody RoleWithAuthority roleWithAuthority) {
        securityService.addRoleWithAuthority(roleWithAuthority);
        return new ResultMessage<>();
    }

    @ApiOperation("更新角色及其权限关联")
    @PostMapping("/updateRoleWithAuthority")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.ROLE_UPDATE + "')")
    public ResultMessage<?> updateRoleWithAuthority(
            @RequestBody RoleWithAuthority roleWithAuthority) {
        securityService.updateRoleWithAuthority(roleWithAuthority);
        return new ResultMessage<>();
    }

    @ApiOperation("删除角色")
    @PostMapping("/deleteRole")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.ROLE_DELETE + "')")
    public ResultMessage<?> deleteRole(Long roleId) {
        securityService.deleteRole(roleId);
        return new ResultMessage<>();
    }

}
