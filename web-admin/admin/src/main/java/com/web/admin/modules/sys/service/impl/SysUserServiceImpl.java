package com.web.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.web.admin.modules.sys.dto.SysUserDTO;
import com.web.admin.modules.sys.entity.SysUser;
import com.web.admin.modules.sys.entity.SysUserRole;
import com.web.admin.modules.sys.mapper.SysUserMapper;
import com.web.admin.modules.sys.service.SysUserRoleService;
import com.web.admin.modules.sys.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.admin.utils.PageWrapper;
import com.web.common.utils.AssertUtil;
import com.web.common.utils.Constant;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author zzj
 * @since 2019-09-04
 */
@Service("SysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    SysUserRoleService sysUserRoleService;

    @Override
    public IPage listPage(Map<String, Object> params) {
        IPage<SysUser> sysUserIPage = baseMapper.listPage(new PageWrapper<SysUser>(params).getPage(),params);
        return sysUserIPage;
    }

    @Override
    public void add(SysUserDTO sysUserDTO) {
        SysUser existUser = this.getUserByUsername(sysUserDTO.getUsername());
        AssertUtil.isNull(existUser,"用户已存在");
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserDTO,sysUser);
        String salt = RandomStringUtils.randomAlphanumeric(20);
        sysUser.setPassword(new Sha256Hash(Constant.INITIAL_PASSWORD, salt).toHex());
        sysUser.setSalt(salt);
        sysUser.setCreateBy(1L);
        baseMapper.insert(sysUser);
        sysUserRoleService.saveUserRole(sysUser.getId(),sysUserDTO.getRoleIdList());
    }

    @Override
    public void update(SysUserDTO sysUserDTO) {
        SysUser existUser = this.getUserById(sysUserDTO.getId());
        AssertUtil.notNull(existUser,"用户不存在");
        SysUser sysUser = new SysUser();
        sysUser.setId(sysUserDTO.getId());
        sysUser.setUsername(sysUserDTO.getUsername());
        sysUser.setPhone(sysUserDTO.getPhone());
        sysUser.setEmail(sysUserDTO.getEmail());
        sysUser.setState(sysUserDTO.getState());
        baseMapper.updateById(sysUser);
        sysUserRoleService.saveUserRole(sysUser.getId(),sysUserDTO.getRoleIdList());
    }

    @Override
    public void delete(List<Long> ids) {
        ids.forEach(id->{
            SysUser sysUser = new SysUser();
            sysUser.setId(id);
            sysUser.setDelFlag(1);
            baseMapper.updateById(sysUser);
        });
    }

    public void resetPassword(Long userId){
        SysUser sysUser = new SysUser();
        String salt = RandomStringUtils.randomAlphanumeric(20);
        sysUser.setPassword(new Sha256Hash(Constant.INITIAL_PASSWORD, salt).toHex());
        sysUser.setSalt(salt);
        sysUser.setId(userId);
        baseMapper.updateById(sysUser);
    }

    public SysUser getUserById(Long userId){
        return baseMapper.selectById(userId);
    }

    public SysUser getUserByUsername(String username){
        return baseMapper.selectOne(new QueryWrapper<SysUser>().eq("username",username));
    }
}