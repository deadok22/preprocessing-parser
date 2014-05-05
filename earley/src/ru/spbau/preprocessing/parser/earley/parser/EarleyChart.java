package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

import java.util.List;

class EarleyChart {
  private final List<EarleyChartColumn> myChart;
  // maps indexes in this chart's column list to subcharts created at these indexes.
  private final HashMultimap<Integer, EarleyChart> mySubCharts = HashMultimap.create();

  public EarleyChart() {
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

  public EarleyChart createSubChart() {
    EarleyChart subChart = new EarleyChart();
    subChart.myChart.add(lastColumn());
    mySubCharts.put(myChart.size() - 1, subChart);
    return subChart;
  }

  EarleyChartColumn getColumnBefore(EarleyChartColumn column) {
    int i = myChart.indexOf(column);
    return i > 0 ? myChart.get(i - 1) : null;
  }
}
