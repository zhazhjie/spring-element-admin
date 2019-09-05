package com.web.admin.modules.sys.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.web.admin.modules.sys.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author zzj
 * @since 2019-09-04
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
    IPage<SysUser> listPage(IPage page, @Param("params") Map<String, Object> params);
}