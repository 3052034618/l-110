package com.carbon.management.config;

import com.carbon.management.common.enums.BudgetStatus;
import com.carbon.management.common.enums.EmissionScope;
import com.carbon.management.common.enums.TaskCategory;
import com.carbon.management.common.enums.TaskStatus;
import com.carbon.management.entity.BudgetDecomposition;
import com.carbon.management.entity.CarbonBudget;
import com.carbon.management.entity.EmissionTask;
import com.carbon.management.repository.BudgetDecompositionRepository;
import com.carbon.management.repository.CarbonBudgetRepository;
import com.carbon.management.repository.EmissionTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CarbonBudgetRepository budgetRepository;
    private final BudgetDecompositionRepository decompositionRepository;
    private final EmissionTaskRepository taskRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (budgetRepository.count() > 0) {
            log.info("数据库已有数据，跳过初始化");
            return;
        }

        log.info("开始初始化演示数据...");

        int currentYear = LocalDate.now().getYear();

        CarbonBudget annualBudget = new CarbonBudget();
        annualBudget.setBudgetCode("BGT-ORG001-" + currentYear + "-ANNUAL");
        annualBudget.setBudgetName(currentYear + "年度总碳预算");
        annualBudget.setOrgId("ORG001");
        annualBudget.setOrgName("集团总部");
        annualBudget.setBudgetYear(currentYear);
        annualBudget.setBudgetMonth(null);
        annualBudget.setEmissionScope(EmissionScope.SCOPE_1);
        annualBudget.setTotalBudget(new BigDecimal("10000.0000"));
        annualBudget.setUsedAmount(new BigDecimal("3500.0000"));
        annualBudget.setRemainingAmount(new BigDecimal("6500.0000"));
        annualBudget.setWarningThreshold(new BigDecimal("80.00"));
        annualBudget.setDangerThreshold(new BigDecimal("95.00"));
        annualBudget.setStatus(BudgetStatus.APPROVED);
        annualBudget.setDescription("年度总碳排放预算，涵盖全公司所有直接排放");
        annualBudget.setCreatedBy("admin");
        annualBudget = budgetRepository.save(annualBudget);

        CarbonBudget scope2Budget = new CarbonBudget();
        scope2Budget.setBudgetCode("BGT-ORG001-" + currentYear + "-SCOPE2");
        scope2Budget.setBudgetName(currentYear + "年度范围2预算");
        scope2Budget.setOrgId("ORG001");
        scope2Budget.setOrgName("集团总部");
        scope2Budget.setBudgetYear(currentYear);
        scope2Budget.setEmissionScope(EmissionScope.SCOPE_2);
        scope2Budget.setTotalBudget(new BigDecimal("5000.0000"));
        scope2Budget.setUsedAmount(new BigDecimal("2800.0000"));
        scope2Budget.setRemainingAmount(new BigDecimal("2200.0000"));
        scope2Budget.setWarningThreshold(new BigDecimal("80.00"));
        scope2Budget.setDangerThreshold(new BigDecimal("95.00"));
        scope2Budget.setStatus(BudgetStatus.APPROVED);
        scope2Budget.setDescription("外购电力产生的间接排放预算");
        scope2Budget.setCreatedBy("admin");
        scope2Budget = budgetRepository.save(scope2Budget);

        for (int month = 1; month <= 6; month++) {
            CarbonBudget monthlyBudget = new CarbonBudget();
            monthlyBudget.setBudgetCode(String.format("BGT-ORG001-%d-M%02d", currentYear, month));
            monthlyBudget.setBudgetName(String.format("%d年%d月预算", currentYear, month));
            monthlyBudget.setOrgId("ORG001");
            monthlyBudget.setOrgName("集团总部");
            monthlyBudget.setBudgetYear(currentYear);
            monthlyBudget.setBudgetMonth(month);
            monthlyBudget.setEmissionScope(EmissionScope.SCOPE_1);
            monthlyBudget.setTotalBudget(new BigDecimal("800.0000"));
            monthlyBudget.setUsedAmount(new BigDecimal(month * 100 + 200));
            monthlyBudget.setRemainingAmount(new BigDecimal("800.0000").subtract(new BigDecimal(month * 100 + 200)));
            monthlyBudget.setWarningThreshold(new BigDecimal("80.00"));
            monthlyBudget.setDangerThreshold(new BigDecimal("95.00"));
            monthlyBudget.setStatus(BudgetStatus.APPROVED);
            monthlyBudget.setParentId(annualBudget.getId());
            monthlyBudget.setCreatedBy("admin");
            budgetRepository.save(monthlyBudget);
        }

        BudgetDecomposition dept1Dec = new BudgetDecomposition();
        dept1Dec.setBudgetId(annualBudget.getId());
        dept1Dec.setDeptId("DEPT001");
        dept1Dec.setDeptName("生产一部");
        dept1Dec.setTargetReduction(new BigDecimal("4000.0000"));
        dept1Dec.setAchievedReduction(new BigDecimal("1800.0000"));
        dept1Dec.setCompletionRate(new BigDecimal("45.00"));
        dept1Dec.setResponsibilityPerson("张三");
        dept1Dec.setRemark("生产车间减排责任部门");
        dept1Dec.setCreatedBy("admin");
        dept1Dec = decompositionRepository.save(dept1Dec);

        BudgetDecomposition dept2Dec = new BudgetDecomposition();
        dept2Dec.setBudgetId(annualBudget.getId());
        dept2Dec.setDeptId("DEPT002");
        dept2Dec.setDeptName("能源管理部");
        dept2Dec.setTargetReduction(new BigDecimal("3000.0000"));
        dept2Dec.setAchievedReduction(new BigDecimal("1200.0000"));
        dept2Dec.setCompletionRate(new BigDecimal("40.00"));
        dept2Dec.setResponsibilityPerson("李四");
        dept2Dec.setRemark("能源优化与绿色电力采购");
        dept2Dec.setCreatedBy("admin");
        dept2Dec = decompositionRepository.save(dept2Dec);

        BudgetDecomposition dept3Dec = new BudgetDecomposition();
        dept3Dec.setBudgetId(annualBudget.getId());
        dept3Dec.setDeptId("DEPT003");
        dept3Dec.setDeptName("采购部");
        dept3Dec.setTargetReduction(new BigDecimal("2000.0000"));
        dept3Dec.setAchievedReduction(new BigDecimal("500.0000"));
        dept3Dec.setCompletionRate(new BigDecimal("25.00"));
        dept3Dec.setResponsibilityPerson("王五");
        dept3Dec.setRemark("绿色采购与供应链减排");
        dept3Dec.setCreatedBy("admin");
        decompositionRepository.save(dept3Dec);

        EmissionTask task1 = new EmissionTask();
        task1.setTaskCode("TSK-20240115-0001");
        task1.setTaskName("生产线电机节能改造");
        task1.setBudgetId(annualBudget.getId());
        task1.setDecompositionId(dept1Dec.getId());
        task1.setOrgId("ORG001");
        task1.setOrgName("集团总部");
        task1.setDeptId("DEPT001");
        task1.setDeptName("生产一部");
        task1.setEmissionScope(EmissionScope.SCOPE_1);
        task1.setCategory(TaskCategory.ENERGY_SAVING);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setEstimatedReduction(new BigDecimal("1200.0000"));
        task1.setActualReduction(new BigDecimal("680.0000"));
        task1.setCompletionRate(new BigDecimal("56.67"));
        task1.setStartDate(LocalDate.of(currentYear, 1, 10));
        task1.setEndDate(LocalDate.of(currentYear, 12, 31));
        task1.setResponsibilityPerson("张三");
        task1.setDescription("将生产车间老旧电机更换为高效节能电机，预计节电30%");
        task1.setMeasureDetail("更换100台高效电机，加装变频器，优化运行方案");
        task1.setCreatedBy("admin");
        taskRepository.save(task1);

        EmissionTask task2 = new EmissionTask();
        task2.setTaskCode("TSK-20240201-0002");
        task2.setTaskName("屋顶光伏电站建设");
        task2.setBudgetId(scope2Budget.getId());
        task2.setDecompositionId(dept2Dec.getId());
        task2.setOrgId("ORG001");
        task2.setOrgName("集团总部");
        task2.setDeptId("DEPT002");
        task2.setDeptName("能源管理部");
        task2.setEmissionScope(EmissionScope.SCOPE_2);
        task2.setCategory(TaskCategory.FUEL_SUBSTITUTION);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setEstimatedReduction(new BigDecimal("800.0000"));
        task2.setActualReduction(new BigDecimal("320.0000"));
        task2.setCompletionRate(new BigDecimal("40.00"));
        task2.setStartDate(LocalDate.of(currentYear, 2, 1));
        task2.setEndDate(LocalDate.of(currentYear, 10, 31));
        task2.setResponsibilityPerson("李四");
        task2.setDescription("在厂区建筑屋顶建设分布式光伏电站");
        task2.setMeasureDetail("装机容量2MW，预计年发电量200万kWh");
        task2.setCreatedBy("admin");
        taskRepository.save(task2);

        EmissionTask task3 = new EmissionTask();
        task3.setTaskCode("TSK-20240310-0003");
        task3.setTaskName("绿色包装材料推广");
        task3.setBudgetId(annualBudget.getId());
        task3.setDecompositionId(dept3Dec.getId());
        task3.setOrgId("ORG001");
        task3.setOrgName("集团总部");
        task3.setDeptId("DEPT003");
        task3.setDeptName("采购部");
        task3.setEmissionScope(EmissionScope.SCOPE_3);
        task3.setCategory(TaskCategory.GREEN_PROCUREMENT);
        task3.setStatus(TaskStatus.NOT_STARTED);
        task3.setEstimatedReduction(new BigDecimal("500.0000"));
        task3.setActualReduction(BigDecimal.ZERO);
        task3.setCompletionRate(BigDecimal.ZERO);
        task3.setStartDate(LocalDate.of(currentYear, 3, 15));
        task3.setEndDate(LocalDate.of(currentYear, 12, 31));
        task3.setResponsibilityPerson("王五");
        task3.setDescription("推广使用可降解包装材料，减少塑料包装使用");
        task3.setMeasureDetail("与3家包装供应商签订绿色采购协议");
        task3.setCreatedBy("admin");
        taskRepository.save(task3);

        EmissionTask task4 = new EmissionTask();
        task4.setTaskCode("TSK-20240105-0004");
        task4.setTaskName("LED照明改造工程");
        task4.setBudgetId(scope2Budget.getId());
        task4.setDecompositionId(dept2Dec.getId());
        task4.setOrgId("ORG001");
        task4.setOrgName("集团总部");
        task4.setDeptId("DEPT002");
        task4.setDeptName("能源管理部");
        task4.setEmissionScope(EmissionScope.SCOPE_2);
        task4.setCategory(TaskCategory.ENERGY_SAVING);
        task4.setStatus(TaskStatus.COMPLETED);
        task4.setEstimatedReduction(new BigDecimal("300.0000"));
        task4.setActualReduction(new BigDecimal("315.0000"));
        task4.setCompletionRate(new BigDecimal("105.00"));
        task4.setStartDate(LocalDate.of(currentYear, 1, 5));
        task4.setEndDate(LocalDate.of(currentYear, 3, 31));
        task4.setResponsibilityPerson("李四");
        task4.setDescription("全厂照明系统更换为LED节能灯具");
        task4.setMeasureDetail("更换5000盏LED灯，安装智能照明控制系统");
        task4.setCreatedBy("admin");
        taskRepository.save(task4);

        log.info("演示数据初始化完成：{}条预算, {}条分解记录, {}条任务",
                budgetRepository.count(),
                decompositionRepository.count(),
                taskRepository.count());
    }
}
