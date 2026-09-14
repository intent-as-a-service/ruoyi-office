-- =============================================
-- 通知公告示例数据
-- 执行时间：2025-01-XX
-- 说明：用于测试公告系统功能的示例数据
-- =============================================

-- 1. 2026年费用报销制度（规章制度类型，value=4）
INSERT INTO `system_notice` (`title`, `content`, `type`, `status`, `is_important`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES 
('关于发布《2026年度费用报销管理制度》的通知', 
'<h2 style="text-align: center; margin-bottom: 20px;">关于发布《2026年度费用报销管理制度》的通知</h2>

<p style="text-indent: 2em; line-height: 1.8;">各部门、各子公司：</p>

<p style="text-indent: 2em; line-height: 1.8;">为进一步规范公司费用报销管理，提高财务管理效率，根据国家相关法律法规及公司实际情况，现发布《2026年度费用报销管理制度》，请各部门认真组织学习，严格遵照执行。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">一、适用范围</h3>
<p style="text-indent: 2em; line-height: 1.8;">本制度适用于公司全体员工因公发生的各项费用报销，包括但不限于：差旅费、交通费、住宿费、餐饮费、通讯费、办公用品采购费等。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">二、报销标准</h3>
<ul style="line-height: 1.8;">
    <li><strong>差旅费：</strong>按职级执行不同标准，普通员工每日住宿标准不超过300元，餐费标准每日不超过150元；</li>
    <li><strong>交通费：</strong>优先选择公共交通工具，确需乘坐出租车的，需提供合理说明；</li>
    <li><strong>通讯费：</strong>按岗位级别设定月度限额，超出部分需提供使用明细；</li>
    <li><strong>办公用品：</strong>单次采购金额超过500元需提前申请审批。</li>
</ul>

<h3 style="margin-top: 20px; margin-bottom: 10px;">三、报销流程</h3>
<ol style="line-height: 1.8;">
    <li>员工填写《费用报销单》，附相关发票及证明材料；</li>
    <li>部门负责人审核签字；</li>
    <li>财务部门审核票据真实性及合规性；</li>
    <li>财务总监审批（单笔金额超过5000元需总经理审批）；</li>
    <li>财务部门办理报销手续，3个工作日内完成付款。</li>
</ol>

<h3 style="margin-top: 20px; margin-bottom: 10px;">四、注意事项</h3>
<ul style="line-height: 1.8;">
    <li>所有报销票据必须真实有效，严禁虚报、假报；</li>
    <li>费用发生时间与报销时间间隔不得超过3个月；</li>
    <li>电子发票需打印并加盖"已报销"印章；</li>
    <li>报销单填写不规范或缺少必要附件的，将退回重新办理。</li>
</ul>

<h3 style="margin-top: 20px; margin-bottom: 10px;">五、生效时间</h3>
<p style="text-indent: 2em; line-height: 1.8;">本制度自2026年1月1日起正式实施，原《2025年度费用报销管理制度》同时废止。</p>

<p style="text-indent: 2em; line-height: 1.8; margin-top: 20px;">特此通知。</p>

<p style="text-align: right; margin-top: 30px;">财务部<br>2025年12月15日</p>', 
4, 0, b'1', 'admin', NOW(), 'admin', NOW(), b'0', 1);

-- 2. 新闻动态公告（公司动态类型，value=2）
INSERT INTO `system_notice` (`title`, `content`, `type`, `status`, `is_important`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES 
('公司成功举办2025年度总结表彰大会', 
'<h2 style="text-align: center; margin-bottom: 20px;">公司成功举办2025年度总结表彰大会</h2>

<p style="text-indent: 2em; line-height: 1.8;">2025年12月20日，公司在总部大会议室隆重举行2025年度总结表彰大会。公司董事长、总经理及各部门负责人、优秀员工代表等200余人参加了此次盛会。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">一、年度业绩回顾</h3>
<p style="text-indent: 2em; line-height: 1.8;">会上，总经理首先对2025年度公司整体运营情况进行了全面总结。2025年，公司实现营业收入同比增长35%，净利润增长42%，各项经营指标均创历史新高。在技术创新方面，公司新增专利15项，获得省级科技进步奖2项，研发投入占比达到8.5%。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">二、优秀团队表彰</h3>
<p style="text-indent: 2em; line-height: 1.8;">大会对2025年度表现突出的团队和个人进行了表彰：</p>
<ul style="line-height: 1.8;">
    <li><strong>优秀团队奖：</strong>研发部、市场部、客服部</li>
    <li><strong>创新团队奖：</strong>产品创新小组、技术攻关小组</li>
    <li><strong>销售冠军：</strong>华东区域销售团队</li>
    <li><strong>服务之星：</strong>客户服务中心</li>
</ul>

<h3 style="margin-top: 20px; margin-bottom: 10px;">三、先进个人表彰</h3>
<p style="text-indent: 2em; line-height: 1.8;">本次大会共表彰优秀员工50名，其中：</p>
<ul style="line-height: 1.8;">
    <li>优秀管理者10名</li>
    <li>优秀员工30名</li>
    <li>技术能手5名</li>
    <li>服务标兵5名</li>
</ul>
<p style="text-indent: 2em; line-height: 1.8;">获奖员工代表在发言中表示，将珍惜荣誉，再接再厉，为公司发展贡献更大力量。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">四、2026年展望</h3>
<p style="text-indent: 2em; line-height: 1.8;">董事长在总结讲话中强调，2026年公司将围绕"创新驱动、质量优先、客户至上"的发展理念，继续加大研发投入，拓展市场领域，提升服务质量，力争实现营业收入再增长30%的目标。同时，公司将进一步完善员工激励机制，为员工提供更好的发展平台和福利待遇。</p>

<p style="text-indent: 2em; line-height: 1.8; margin-top: 20px;">大会在热烈的掌声中圆满结束。全体参会人员合影留念，共同记录这一重要时刻。</p>

<p style="text-align: right; margin-top: 30px;">行政部<br>2025年12月21日</p>', 
2, 0, b'0', 'admin', NOW(), 'admin', NOW(), b'0', 1);

-- 3. 行业咨询公告（行业咨询类型，value=3）
INSERT INTO `system_notice` (`title`, `content`, `type`, `status`, `is_important`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES 
('2026年企业管理数字化转型趋势分析', 
'<h2 style="text-align: center; margin-bottom: 20px;">2026年企业管理数字化转型趋势分析</h2>

<p style="text-indent: 2em; line-height: 1.8;">随着人工智能、大数据、云计算等技术的快速发展，企业管理数字化转型已成为不可逆转的趋势。根据最新行业研究报告，2026年企业管理将呈现以下重要趋势：</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">一、智能化办公成为主流</h3>
<p style="text-indent: 2em; line-height: 1.8;">AI技术在企业管理中的应用将更加深入，智能审批、智能客服、智能数据分析等功能将大幅提升办公效率。预计到2026年底，超过70%的企业将部署AI辅助办公系统，员工工作效率有望提升40%以上。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">二、数据驱动决策模式普及</h3>
<p style="text-indent: 2em; line-height: 1.8;">企业将更加重视数据资产的积累和利用，通过大数据分析平台实现精准决策。实时数据看板、预测性分析、智能报表等工具将成为管理层的标配，帮助企业快速响应市场变化。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">三、云端协同办公常态化</h3>
<p style="text-indent: 2em; line-height: 1.8;">远程办公和混合办公模式将继续发展，云端协同平台将成为企业基础设施的重要组成部分。视频会议、在线协作、文档共享等功能将进一步完善，打破地域限制，实现真正的"随时随地办公"。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">四、信息安全要求提升</h3>
<p style="text-indent: 2em; line-height: 1.8;">随着数字化转型的深入，企业面临的信息安全风险也在增加。数据加密、身份认证、访问控制、安全审计等将成为企业IT建设的重点。预计2026年，企业在信息安全方面的投入将增长25%以上。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">五、员工体验优化</h3>
<p style="text-indent: 2em; line-height: 1.8;">企业将更加关注员工体验，通过数字化手段简化工作流程，减少重复性工作，让员工专注于更有价值的创造性工作。移动办公、自助服务、个性化工作台等将成为提升员工满意度的重要手段。</p>

<h3 style="margin-top: 20px; margin-bottom: 10px;">六、建议与展望</h3>
<p style="text-indent: 2em; line-height: 1.8;">面对数字化转型的浪潮，建议企业：</p>
<ol style="line-height: 1.8;">
    <li>制定清晰的数字化转型战略，分阶段实施；</li>
    <li>加强员工数字化技能培训，提升全员数字化素养；</li>
    <li>选择成熟稳定的数字化平台，避免重复建设；</li>
    <li>建立数据治理体系，确保数据质量和安全；</li>
    <li>持续关注行业动态，及时调整转型策略。</li>
</ol>

<p style="text-indent: 2em; line-height: 1.8; margin-top: 20px;">数字化转型不是一蹴而就的过程，需要企业持续投入和不断优化。只有紧跟时代步伐，才能在激烈的市场竞争中立于不败之地。</p>

<p style="text-align: right; margin-top: 30px;">信息部<br>2025年12月18日</p>', 
3, 0, b'0', 'admin', NOW(), 'admin', NOW(), b'0', 1);
