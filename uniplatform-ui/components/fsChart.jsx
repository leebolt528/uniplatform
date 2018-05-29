import React, { Component } from 'react';
import { Row, Col, Text} from 'epm-ui';

const tips = [
  { key: 'committed', value: 120 },
  { key: 'used', value: 446.5 }
];

class FsChart extends Component {

  constructor(props) {
    super(props);

    this.state = {
        chart: null,
        info: null
    }

  }

  // 实例化图表
  componentDidMount() {
    let name = this.props.id;
    let chartName = this.props.chartName;
    let locIndex = this.props.locIndex;

    this.setState({
      chart: bigdesk_charts[chartName].chart( d3.select('#svg_'+ name +'').attr('clip_id', 'clip_'+ name +'') ),
      // chart: bigdesk_charts.disk_reads_writes_cnt.chart( d3.select('#svg_fsChart_cnt_0').attr('clip_id', 'clip_fsChart_cnt_0') ),
      info: templates[chartName](locIndex)
    })

  }

  // 定时更新数据
  componentWillUpdate() {

    let chart = this.state.chart;

    let name = this.props.id;
    let chartName = this.props.chartName;  // 确定调用哪个图表
    let locIndex = this.props.locIndex;  // 图表定位
    let stats = this.props.stats;
    let number = this.props.number;
    let dataExist = this.props.dataExist;

    if (stats.length > 0) {

      if (dataExist) {
        // 更新图表下面的描述信息
        bigdesk_charts[chartName].describe(stats[stats.length-1], locIndex, dataExist);

        // 存在数据
        let data1 = bigdesk_charts[chartName].series1(stats, locIndex);
        let data2 = bigdesk_charts[chartName].series2(stats, locIndex);

        try {

          if (+number == 3) {
            let data3 = bigdesk_charts[chartName].series3(stats, locIndex);

            chart.animate(true).update(data1, data2, data3);
          }else {

            chart.animate(true).update(data1, data2);
          }

        } catch (ignore) {

        }

      }else if (typeof(dataExist) != "undefined"){

        if (!chart) {
          chart = bigdesk_charts[chartName].chart( d3.select('#svg_'+ name +'') );  // 为解决bug:没有数据时，chart为null
        }
        // 没数据
        chart = bigdesk_charts.not_available.chart(chart.svg());

        // 更新图表下面的描述信息
        bigdesk_charts[chartName].describe(stats[stats.length-1], locIndex, dataExist);
      }

    }

  }

  render() {
    let stats = this.props.stats;
    return (
      <div>
        <svg id={'svg_' + this.props.id} preserveAspectRatio="xMinYMid" viewBox="0 0 270 160" ></svg>
        <div dangerouslySetInnerHTML={{ __html: this.state.info}} >
        </div>
      </div>
    );

  }

}

export default FsChart;
