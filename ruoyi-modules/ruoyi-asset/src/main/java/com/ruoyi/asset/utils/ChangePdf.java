package com.ruoyi.asset.utils;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.ruoyi.asset.constant.DictConstants;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.domain.vo.ChangeVO;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.workflow.api.RemoteWorkflowService;
import com.ruoyi.workflow.api.constant.TaskDefinitionConstants;
import com.ruoyi.workflow.api.domain.vo.ApprovalHistoryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.ruoyi.asset.utils.PdfUtils.addCell;
import static com.ruoyi.asset.utils.PdfUtils.addCenterCell;

/**
 * 资产变动单PDF导出工具类
 * 用于生成资产变动申请的PDF文档，包含基本信息、资产清单、二维码、条码、签名和水印
 *
 * @author ruoyi
 */
@Component
public class ChangePdf {


    @Autowired
    private RemoteWorkflowService remoteWorkflowService;

    private static final Logger log = LoggerFactory.getLogger(ChangePdf.class);
    @Autowired
    private UserUtils userUtils;

    /**
     * 导出资产变动单PDF文件
     * 根据变动单信息生成完整的PDF文档，包含标题、二维码、条码、信息表格、资产清单、签名和水印
     *
     * @param response HttpServletResponse对象，用于输出PDF文件
     * @param change   资产变动单信息
     */
    public void exportPdf(HttpServletResponse response, ChangeVO change) {
        // 1.设置响应头
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");

        // 2.流式API创建PDF文档（使用A4横向）
        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4.rotate())) {

            // 3.设置文档边距：上20、右15、下20、左15
            document.setMargins(20, 15, 20, 15);

            // 4.创建字体（仿宋、楷体、黑体）
            PdfFont fangsongFont = FontUtils.createFangsongFont();
            PdfFont kaiFont = FontUtils.createKaiFont();
            PdfFont heiFont = FontUtils.createHeiFont();

            // 5.添加主标题、二维码和条码
            // 5.1 将变动类型由字典值转为字典标签作为pdf文档主标题
            String title = DictLabelUtils.getDictLabel(DictConstants.CHANGE_TYPE, change.getChangeType());
            PdfUtils.addMainTitle(title, document, heiFont);
            // 5.2 构建二维码内容并生成二维码 todo扫码暂时识别不了中文待解决
            String qrCodeContent = "change_code:" + change.getChangeCode() + "\n"
                    + "change_type:" + change.getChangeType() + "\n";
            PdfUtils.addQrCodeToTopLeft(qrCodeContent, pdf, document);
            // 5.3 生成条码
            PdfUtils.addBarcodeToTopRight(change.getChangeCode(), pdf, document);

            // 6.添加基本信息章节和表格
            PdfUtils.addSectionTitle(document, heiFont, "基本信息");
            document.add(createBasicInfoTable(change, kaiFont));

            // 7.添加资产清单章节和表格
            PdfUtils.addSectionTitle(document, heiFont, "资产清单");
            document.add(createAssetLedgerPanel(change, kaiFont));

            // 8.添加审批人签名图片
            String imagePath = Objects.requireNonNull(getClass().getResource("/images/zhangsan.png")).getPath();
            PdfUtils.addSignatureToBottomRight(document, imagePath, 1);

            // 9.添加文字水印
            PdfUtils.addWatermark(pdf, "资产管理系统", fangsongFont);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建基本信息表格
     * 展示变动单的基本信息，包括变动单编码、申请人、申请时间、变动原因等
     * 如果流程已结束，还会显示审批人、审批时间和审批状态
     *
     * @param change 资产变动单信息
     * @param font   使用的字体
     * @return 基本信息表格对象
     */
    private Table createBasicInfoTable(ChangeVO change, PdfFont font) {
        // 1.创建8列表格，列宽比例为1:2:1:2:1:2
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2, 1, 2, 1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // 2.添加变动单编码、申请人、申请时间
        PdfUtils.addRow8(table, font,
                "变动单编码", change.getChangeCode(),
                "申请人", change.getApplicantName(),
                "申请部门", change.getApplicantDeptName(),
                "申请时间", DateUtils.parseDateToStr("yyyy-MM-dd", change.getApplyDate()));

        // 3.添加变动原因
        PdfUtils.addCellWithColspan(table, font, "变动原因", CommonUtils.nullToEmpty(change.getChangeReason()), 7);

        // 4.添加备注
        PdfUtils.addCellWithColspan(table, font, "备注", CommonUtils.nullToEmpty(change.getRemark()), 7);

        // 5.获取审批信息（如果流程已结束）
        if (StringUtils.isNotEmpty(change.getProcInstId())) {
            try {
                // 5.1 检查流程是否已结束
                R<Boolean> endedResult = remoteWorkflowService.isProcessEnded(change.getProcInstId());
                log.debug("导出资产变动pdf - 检查流程是否结束，结束？(true or false){}", endedResult.getData());
                if (endedResult.getCode() == 200 && Boolean.TRUE.equals(endedResult.getData())) {
                    // 5.2 获取审批历史记录
                    R<List<ApprovalHistoryVO>> historyResult = remoteWorkflowService.getApprovalHistory(change.getProcInstId());
                    log.debug("导出资产变动pdf - 获取审批历史记录，结果{}:", historyResult.getData());
                    if (historyResult.getCode() == 200 && historyResult.getData() != null) {
                        // 5.3 遍历审批记录，找到最后一个非提交节点的审批信息
                        for (int i = historyResult.getData().size() - 1; i >= 0; i--) {
                            ApprovalHistoryVO item = historyResult.getData().get(i);
                            if (!TaskDefinitionConstants.SUBMIT.equals(item.getTaskDefinitionKey())) {
                                // 5.4 获取审批人姓名
                                String approverName = StringUtils.isNotEmpty(item.getAssigneeName())
                                        ? item.getAssigneeName()
                                        : (StringUtils.isNotEmpty(item.getAssignee()) ? "用户" + item.getAssignee() : "未知");
                                // 5.4 获取审批状态
                                String businessStatus = item.getApproved() != null && item.getApproved() ? "已通过" : "已驳回";
                                // 5.5 添加审批人、审批时间、审批状态
                                PdfUtils.addRow8(table, font,
                                        "审批人", approverName,
                                        "审批时间", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", item.getEndTime()),
                                        "审批状态", businessStatus,
                                        "任务名称",item.getTaskName());
                                // 5.6 添加审批意见
                                PdfUtils.addCellWithColspan(table, font, "审批意见", item.getComment(), 7);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("获取审批信息失败", e);
            }
        }

        return table;
    }

    /**
     * 创建资产明细表格
     * 展示变动单关联的所有资产信息，包括序号、资产编码、资产名称、分类、来源、状态、存放位置
     *
     * @param change 资产变动单信息
     * @param font   使用的字体
     * @return 资产明细表格对象
     */
    private Table createAssetLedgerPanel(ChangeVO change, PdfFont font) {
        // 1.创建7列表格，列宽比例为5:12:15:10:6:6:25
        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 12, 15, 10, 6, 6, 25}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // 2.添加表头
        String[] headers = {"序号", "资产编码", "资产名称","资产分类", "资产状态", "资产来源", "存放位置"};
        for (String header : headers) {
            PdfUtils.addHeaderCell(table, font, header);
        }

        // 3.添加资产数据
        if (change.getAssets() != null && !change.getAssets().isEmpty()) {
            int index = 1;
            for (Assets asset : change.getAssets()) {
                addCenterCell(table, font, String.valueOf(index++));                    // 序号
                addCenterCell(table, font, CommonUtils.nullToEmpty(asset.getAssetCode()));        // 资产编码
                addCenterCell(table, font, CommonUtils.nullToEmpty(asset.getAssetName()));              // 资产名称
                addCenterCell(table, font, "分类" + asset.getCategoryId());         // 资产分类
                addCenterCell(table, font, DictLabelUtils.getDictLabel("asset_status", asset.getAssetStatus())); // 资产状态
                addCenterCell(table, font, DictLabelUtils.getDictLabel("asset_source", asset.getAssetSource())); // 资产来源
                addCell(table, font, CommonUtils.nullToEmpty(asset.getLocation()));               // 存放位置
            }
        } else {
            // 4.无数据时显示提示
            Cell emptyCell = new Cell(1, 7)
                    .add(new Paragraph("暂无资产数据").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER);
            table.addCell(emptyCell);
        }

        return table;
    }

}