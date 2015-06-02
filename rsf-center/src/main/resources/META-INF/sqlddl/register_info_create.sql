<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  
<mapper namespace="sp_rsf_class">

	<resultMap id="registerInfoDOMap" type="net.hasor.rsf.center.domain.entity.RegisterInfoDO">
		<result property="serviceID"     column="serviceID" />
		<result property="terminalID"    column="terminalID" />
		<result property="timeout"       column="timeout" />
		<result property="serializeType" column="serializeType" />
		<result property="persona"       column="persona" />
	</resultMap>

	<sql id="registerInfoDOMap_allColumns">
		*
	</sql>

	<select id="registerInfoDO_getALL" resultMap="registerInfoDOMap">
		select
			<include refid="registerInfoDOMap_allColumns" />
		from bx_info
	</select>

</mapper>