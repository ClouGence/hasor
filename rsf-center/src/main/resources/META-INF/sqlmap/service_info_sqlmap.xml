<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  
<mapper namespace="sp_rsf_class">

	<resultMap id="serviceInfoDOMap" type="net.hasor.rsf.center.domain.entity.ServiceInfoDO">
		<result property="appID"       column="appID" />
		<result property="bindID"      column="bindID" />
		<result property="bindName"    column="bindName" />
		<result property="bindGroup"   column="bindGroup" />
		<result property="bindVersion" column="bindVersion" />
		<result property="bindType"    column="bindType" />
		<result property="hashCode"    column="hashCode" />
	</resultMap>

	<sql id="serviceInfoDOMap_allColumns">
		*
	</sql>

	<select id="serviceInfoDO_getALL" resultMap="serviceInfoDOMap">
		select
			<include refid="serviceInfoDOMap_allColumns" />
		from bx_info
	</select>

</mapper>