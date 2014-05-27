package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;

import java.util.List;

class EarleyChart {
  private final EarleyChartColumn myParentColumnBeforeFork;

  private final List<EarleyChartColumn> myChart;
  // maps indexes in this chart's column list to subcharts created at these indexes.
  private final LinkedHashMultimap<Integer, EarleyChart> mySubCharts = LinkedHashMultimap.create();
  // condition of a conditional branch corresponding to this chart
  private final PresenceCondition myBasePresenceCondition;

  public EarleyChart(PresenceCondition basePresenceCondition) {
    this(null, basePresenceCondition);
  }

  private EarleyChart(EarleyChartColumn parentColumnBeforeFork, PresenceCondition presenceCondition) {
    myParentColumnBeforeFork = parentColumnBeforeFork;
    myBasePresenceCondition = presenceCondition;
    myChart = Lists.newArrayList();
  }

  public EarleyChartColumn newColumn() {
    EarleyChartColumn column = new EarleyChartColumn(this);
    myChart.add(column);
    return column;
  }

  public EarleyChartColumn lastColumn() {
    return myChart.isEmpty() ? null : myChart.get(myChart.size() - 1);
  }

  public boolean isFirstColumn(EarleyChartColumn chartColumn) {
    return !myChart.isEmpty() && chartColumn == myChart.get(0);
  }

  public EarleyChart createSubChart(PresenceCondition presenceCondition) {
    EarleyChart subChart = new EarleyChart(presenceCondition);
    //TODO correct determination of terminals' presence conditions (alter presence conditions of rules which consumed a terminal?)
    EarleyChartColumn subChartFirstColumn = subChart.newColumn();
    subChartFirstColumn.addAllFrom(lastColumn(), presenceCondition);
    mySubCharts.put(myChart.size() - 1, subChart);
    return subChart;
  }

  EarleyChartColumn getColumnBefore(EarleyChartColumn column) {
    int i = myChart.indexOf(column);
    return i > 0 ? myChart.get(i - 1) :
            (myParentColumnBeforeFork != null ? myParentColumnBeforeFork.getChart().getColumnBefore(myParentColumnBeforeFork) : null);
  }

  public PresenceCondition getBasePresenceCondition() {
    return myBasePresenceCondition;
  }
}
