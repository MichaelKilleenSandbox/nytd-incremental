package gov.hhs.acf.cb.nytd.models.helper; /**
 * 
 */
//package gov.hhs.acf.cb.nytd.models.helper;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

import java.text.DecimalFormat;

public class BarChartCustomizer implements JRChartCustomizer
{

	public void customize(JFreeChart chart, JRChart jasperChart)
	{
		BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
		// set color of bar
//		renderer.setSeriesPaint(0, Color.green);
		
		// set maximum width to 5% of chart
		renderer.setMaximumBarWidth(.05); 
		
		// set category label wide enough to be visible and wrap
		CategoryPlot categoryplot = (CategoryPlot)chart.getPlot();
		CategoryAxis domainAxis = categoryplot.getDomainAxis();
		domainAxis.setMaximumCategoryLabelWidthRatio(2.5F);
//		domainAxis.setMaximumCategoryLabelLines(2);
//		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//		domainAxis.setCategoryLabelPositions(
//				CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		
		// set axis to show %
		CategoryPlot plot = (CategoryPlot) chart.getPlot(); 
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		DecimalFormat pctFormat = new DecimalFormat("#'%'");
		rangeAxis.setNumberFormatOverride(pctFormat);

	}
}