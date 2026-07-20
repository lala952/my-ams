<template>
<!--  TODO 待完善，startDate 2026年5月22日17:26:03-->
  <div class="service">
    <el-tooltip content="《资产管理系统使用手册》">
      <el-button text type="primary" icon="Service" size="large" @click="handleService"></el-button>
    </el-tooltip>

    <el-drawer size="80%" v-model="openService" title="《资产管理系统使用手册》| TODO 待完善，startDate 2026年5月22日17:26:03" append-to-body>
      <!-- 一、核心概念 -->
      <h4>一、核心概念</h4>
      <el-table :data="conceptData" border>
        <el-table-column label="概念" prop="name"/>
        <el-table-column prop="definition" label="定义"/>
        <el-table-column prop="example" label="示例" width="180"/>
      </el-table>

      <!-- 二、资产状态说明 -->
      <h4>二、资产状态说明</h4>
      <el-table :data="statusData" border>
        <el-table-column prop="status" label="状态" width="100"/>
        <el-table-column label="编码" prop="code"/>
        <el-table-column prop="description" label="说明"/>
      </el-table>

      <!-- 三、资产类型与来源 -->
      <h4>三、资产类型与来源</h4>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-table :data="assetTypeData" border>
            <el-table-column prop="value" label="资产类型编码" width="140"/>
            <el-table-column prop="label" label="资产类型名称"/>
          </el-table>
        </el-col>
        <el-col :span="12">
          <el-table :data="assetSourceData" border>
            <el-table-column prop="value" label="资产来源编码" width="160"/>
            <el-table-column prop="label" label="资产来源名称"/>
          </el-table>
        </el-col>
      </el-row>

      <!-- 四、折旧方法与公式 -->
      <h4>四、折旧方法与公式</h4>

      <h4 style="margin: 12px 0 8px 0">1. 折旧年限参考（税法最低年限）</h4>
      <el-table :data="depreciationYearsData" border style="margin-bottom: 16px">
        <el-table-column prop="category" label="资产类别" width="180"/>
        <el-table-column prop="years" label="最低年限" width="100"/>
        <el-table-column prop="suggestion" label="系统建议"/>
      </el-table>

      <h4 style="margin: 12px 0 8px 0">2. 四种折旧方法</h4>
      <el-table :data="depreciationMethodData" border style="margin-bottom: 16px">
        <el-table-column prop="name" label="方法名称" width="140"/>
        <el-table-column prop="formula" label="计算公式"/>
        <el-table-column prop="applicable" label="适用场景"/>
      </el-table>

      <h4 style="margin: 12px 0 8px 0">3. 平均年限法（直线法）计算案例</h4>
      <el-table :data="straightLineCase" border style="margin-bottom: 16px">
        <el-table-column prop="year" label="年份" width="80"/>
        <el-table-column prop="depreciation" label="年折旧额（万元）"/>
        <el-table-column prop="netValue" label="年末净值（万元）"/>
      </el-table>

      <h4 style="margin: 12px 0 8px 0">4. 双倍余额递减法计算案例</h4>
      <el-table :data="doubleDecliningCase" border style="margin-bottom: 16px">
        <el-table-column prop="year" label="年份" width="80"/>
        <el-table-column prop="depreciation" label="年折旧额（万元）"/>
        <el-table-column prop="netValue" label="年末净值（万元）"/>
      </el-table>

      <h4 style="margin: 12px 0 8px 0">5. 年数总和法计算案例</h4>
      <el-table :data="sumOfYearsCase" border style="margin-bottom: 16px">
        <el-table-column prop="year" label="年份" width="80"/>
        <el-table-column prop="depreciation" label="年折旧额（万元）"/>
        <el-table-column prop="netValue" label="年末净值（万元）"/>
      </el-table>

      <h4 style="margin: 12px 0 8px 0">6. 核心公式速查</h4>
      <el-table :data="formulaData" border>
        <el-table-column prop="name" label="计算项" width="160"/>
        <el-table-column prop="formula" label="公式"/>
        <el-table-column prop="example" label="示例" width="220"/>
      </el-table>

      <!-- 五、业务流程概览 -->
      <h4>五、业务流程概览</h4>
      <el-table :data="processData" border>
        <el-table-column label="步骤" prop="step"/>
        <el-table-column prop="name" label="环节名称" width="150"/>
        <el-table-column prop="description" label="说明"/>
      </el-table>

      <!-- 六、审批状态说明 -->
      <h4>六、审批状态说明</h4>
      <el-table :data="approveStatusData" border>
        <el-table-column prop="status" label="状态" width="100"/>
        <el-table-column label="编码" prop="code"/>
        <el-table-column prop="description" label="说明"/>
      </el-table>

      <!-- 七、资产编码说明 -->
      <h4>七、资产编码说明</h4>
      <el-table :data="assetCodeData" border>
        <el-table-column prop="field" label="字段" width="100"/>
        <el-table-column prop="value" label="值"/>
        <el-table-column prop="description" label="说明"/>
      </el-table>

      <!-- 八、常见问题 -->
      <h4>八、常见问题</h4>
      <el-table :data="faqData" border>
        <el-table-column prop="question" label="问题" width="260"/>
        <el-table-column prop="answer" label="回答"/>
      </el-table>

      <!-- 九、法律依据 -->
      <h4>九、法律依据与参考</h4>
      <el-table :data="lawData" border>
        <el-table-column prop="name" label="法规名称" width="280"/>
        <el-table-column prop="authority" label="发布部门" width="140"/>
        <el-table-column prop="description" label="核心内容"/>
      </el-table>
      <div style="margin-top: 16px; padding: 12px; background: #f5f7fa; font-size: 12px; color: #909399">
        本系统折旧规则依据上述法规。请访问财政部官网（www.mof.gov.cn）及国家税务总局官网（www.chinatax.gov.cn）核实最新版本。
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="js">
import {ref} from 'vue'

const openService = ref(false)

function handleService() {
  openService.value = !openService.value
}

// 一、核心概念
const conceptData = [
  {name: '原值', definition: '资产购置时的实际入账价值，折旧计算基准值', example: '199,800.00元'},
  {name: '残值', definition: '资产报废时预计可回收的剩余价值', example: '原值 × 残值率 = 9,990元'},
  {name: '残值率', definition: '残值占原值的百分比，通常3%-5%', example: '5%'},
  {name: '折旧年限', definition: '资产预计可使用年数', example: '5年、10年'},
  {name: '月折旧额', definition: '每个月应计提的折旧金额', example: '1,581.75元'},
  {name: '累计折旧', definition: '开始折旧至今的折旧总额', example: '44,289元'},
  {name: '净值', definition: '资产当前账面价值 = 原值 - 累计折旧', example: '155,511元'}
]

// 二、资产状态
const statusData = [
  {status: '使用中', code: 'IN_USE', description: '资产正常使用中'},
  {status: '闲置', code: 'IDLE', description: '资产闲置未使用'},
  {status: '维修中', code: 'REPAIRING', description: '资产正在维修'},
  {status: '已报废', code: 'SCRAPPED', description: '资产已报废处理'},
  {status: '待入库', code: 'PENDING_IN', description: '资产待入库'},
  {status: '待领用', code: 'PENDING_RECEIVE', description: '资产待领用'}
]

// 三、资产类型与来源
const assetTypeData = [
  {value: 'ELECTRONIC', label: '电子设备'},
  {value: 'VEHICLE', label: '车辆'},
  {value: 'OFFICE', label: '办公用品'},
  {value: 'FURNITURE', label: '家具'},
  {value: 'SOFTWARE', label: '软件'},
  {value: 'BUILDING', label: '房屋建筑'}
]

const assetSourceData = [
  {value: 'PURCHASE', label: '采购'},
  {value: 'SELF_PURCHASE', label: '自购'},
  {value: 'DONATION', label: '捐赠'},
  {value: 'ALLOCATION', label: '调拨'},
  {value: 'FINANCE_LEASE', label: '融资租赁'},
  {value: 'INVENTORY_SURPLUS', label: '盘盈'}
]

// 四、折旧年限参考
const depreciationYearsData = [
  {category: '房屋、建筑物', years: '20年', suggestion: '30年'},
  {category: '生产设备', years: '10年', suggestion: '10年'},
  {category: '器具、工具、家具', years: '5年', suggestion: '5年'},
  {category: '运输工具（汽车）', years: '4年', suggestion: '5年'},
  {category: '电子设备', years: '3年', suggestion: '3年'}
]

// 四种折旧方法
const depreciationMethodData = [
  {name: '平均年限法', formula: '年折旧额 = (原值 - 残值) / 使用年限', applicable: '房屋、家具、价值稳定的资产'},
  {name: '工作量法', formula: '单位折旧额 = (原值 - 残值) / 预计总工作量', applicable: '运输车辆、使用不均衡的设备'},
  {name: '双倍余额递减法', formula: '年折旧率 = 2 / 年限 × 100%', applicable: '电子设备、技术更新快的资产'},
  {name: '年数总和法', formula: '年折旧率 = 尚可使用年限 / 年数总和 × 100%', applicable: '前期收益较高的资产'}
]

// 平均年限法案例（原值100万，残值10万，年限5年）
const straightLineCase = [
  {year: '第1年', depreciation: '18.00', netValue: '82.00'},
  {year: '第2年', depreciation: '18.00', netValue: '64.00'},
  {year: '第3年', depreciation: '18.00', netValue: '46.00'},
  {year: '第4年', depreciation: '18.00', netValue: '28.00'},
  {year: '第5年', depreciation: '18.00', netValue: '10.00'}
]

// 双倍余额递减法案例（原值100万，残值10万，年限5年）
const doubleDecliningCase = [
  {year: '第1年', depreciation: '40.00', netValue: '60.00'},
  {year: '第2年', depreciation: '24.00', netValue: '36.00'},
  {year: '第3年', depreciation: '14.40', netValue: '21.60'},
  {year: '第4年', depreciation: '5.80', netValue: '15.80'},
  {year: '第5年', depreciation: '5.80', netValue: '10.00'}
]

// 年数总和法案例（原值100万，残值10万，年限5年）
const sumOfYearsCase = [
  {year: '第1年', depreciation: '30.00', netValue: '70.00'},
  {year: '第2年', depreciation: '24.00', netValue: '46.00'},
  {year: '第3年', depreciation: '18.00', netValue: '28.00'},
  {year: '第4年', depreciation: '12.00', netValue: '16.00'},
  {year: '第5年', depreciation: '6.00', netValue: '10.00'}
]

// 核心公式速查
const formulaData = [
  {name: '预计净残值', formula: '原值 × 残值率', example: '199,800 × 5% = 9,990元'},
  {name: '应计折旧额', formula: '原值 - 预计净残值', example: '199,800 - 9,990 = 189,810元'},
  {name: '直线法月折旧额', formula: '(原值 - 残值) ÷ 折旧年限 ÷ 12', example: '189,810 ÷ 10 ÷ 12 = 1,581.75元'},
  {name: '双倍余额年折旧率', formula: '2 ÷ 折旧年限 × 100%', example: '2 ÷ 10 × 100% = 20%'},
  {name: '年数总和分母', formula: 'n × (n + 1) ÷ 2', example: '10 × 11 ÷ 2 = 55'},
  {name: '已折旧月数', formula: '(当前年 - 开始年) × 12 + (当前月 - 开始月)', example: '(2026-2024)×12+(5-1) = 28个月'},
  {name: '累计折旧', formula: '月折旧额 × 已折旧月数', example: '1,581.75 × 28 = 44,289元'},
  {name: '净值', formula: '原值 - 累计折旧', example: '199,800 - 44,289 = 155,511元'}
]

// 业务流程
const processData = [
  {step: '1', name: '资产申购', description: '用户提交采购申请'},
  {step: '2', name: '资产验收', description: '到货检验是否合格'},
  {step: '3', name: '资产入库', description: '验收合格后录入资产台账'},
  {step: '4', name: '资产领用', description: '资产分配给使用人'},
  {step: '5', name: '资产交回/报废', description: '资产退出使用'}
]

// 审批状态
const approveStatusData = [
  {status: '待提交', code: 'draft', description: '草稿待提交'},
  {status: '办理中', code: 'pending', description: '审批中'},
  {status: '已退回', code: 'rejected', description: '审批驳回'},
  {status: '已完成', code: 'completed', description: '审批完成'},
  {status: '已批准', code: 'approved', description: '已批准'}
]

// 资产编码说明
const assetCodeData = [
  {field: '前缀', value: 'ZCTZ', description: '资产投资（固定前缀）'},
  {field: '日期', value: '20260513', description: '年月日（2026年5月13日）'},
  {field: '流水号', value: '0100', description: '当日流水号（第100个）'},
  {field: '完整示例', value: 'ZCTZ202605130100', description: '系统自动生成，不可手动修改'}
]

// 常见问题
const faqData = [
  {question: '资产编码怎么生成的？', answer: '系统自动生成，格式为 ZCTZ + 年月日 + 4位流水号'},
  {question: '残值率怎么设置？', answer: '根据资产分类预设默认值，新增资产时可手动调整'},
  {question: '为什么净值显示为0？', answer: '说明累计折旧已等于原值，资产已折旧完毕'},
  {question: '资产状态可以手动修改吗？', answer: '部分状态通过业务单据自动变更，报废状态需要走审批流程'},
  {question: '审批流程卡住了怎么办？', answer: '在"审批中心"查看当前审批节点和审批人，联系对应人员处理'},
  {question: '资产折旧从什么时候开始？', answer: '新增资产当月不计提折旧，从下月起计提'}
]

// 法律依据
const lawData = [
  {
    name: '《企业会计准则第4号——固定资产》',
    authority: '财政部',
    description: '固定资产确认、折旧方法、预计净残值等核心规定'
  },
  {name: '《企业所得税法实施条例》第六十条', authority: '国务院', description: '规定固定资产计算折旧的最低年限'},
  {
    name: '关于设备、器具扣除有关企业所得税政策的公告（2023年第37号）',
    authority: '财政部、税务总局',
    description: '单位价值不超过500万元的设备、器具允许一次性税前扣除'
  }
]
</script>

<style scoped lang="scss">
.service {
  position: fixed;
  top: 25%;
  right: 0%;
  display: flex;
  flex-direction: column;
  z-index: 1000;
}
</style>