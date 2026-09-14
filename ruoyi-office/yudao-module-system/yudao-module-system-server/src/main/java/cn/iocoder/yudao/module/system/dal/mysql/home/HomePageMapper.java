package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePagePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 首页配置 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface HomePageMapper extends BaseMapperX<HomePageDO> {

    default PageResult<HomePageDO> selectPage(HomePagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HomePageDO>()
                .likeIfPresent(HomePageDO::getName, reqVO.getName())
                .eqIfPresent(HomePageDO::getCode, reqVO.getCode())
                .eqIfPresent(HomePageDO::getStatus, reqVO.getStatus())
                .orderByDesc(HomePageDO::getId));
    }

    default HomePageDO selectByCode(String code) {
        return selectOne(HomePageDO::getCode, code);
    }

    default HomePageDO selectDefaultPage() {
        return selectOne(HomePageDO::getCode, "default_workspace");
    }

    /**
     * 查询用户可见的首页列表（包括用户创建的首页和系统默认首页）
     */
    default PageResult<HomePageDO> selectPageByUser(HomePagePageReqVO reqVO, Long userId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HomePageDO>()
                .likeIfPresent(HomePageDO::getName, reqVO.getName())
                .eqIfPresent(HomePageDO::getCode, reqVO.getCode())
                .eqIfPresent(HomePageDO::getStatus, reqVO.getStatus())
                .and(wrapper -> wrapper
                        .eq(HomePageDO::getCode, "default_workspace")  // 系统默认首页
                        .or()
                        .eq(HomePageDO::getCreator, String.valueOf(userId))  // 用户创建的首页
                )
                .orderByDesc(HomePageDO::getId));
    }

}
