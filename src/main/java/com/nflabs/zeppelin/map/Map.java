package com.nflabs.zeppelin.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.zeppelin.helium.Application;
import org.apache.zeppelin.helium.ApplicationArgument;
import org.apache.zeppelin.helium.ApplicationException;
import org.apache.zeppelin.helium.Signal;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.interpreter.data.TableData;
import org.apache.zeppelin.interpreter.dev.ZeppelinApplicationDevServer;
import org.apache.zeppelin.resource.ResourceKey;


public class Map extends Application {

  @Override
  protected void onChange(String name, Object oldObject, Object newObject) {
  }

  @Override
  public void signal(Signal signal) {

  }

  @Override
  public void load() throws ApplicationException, IOException {

  }

  @Override
  public void run(ApplicationArgument arg, InterpreterContext context) throws ApplicationException,
      IOException {
    // get TableData
    TableData tableData = (TableData) context.getResourcePool().get(
        arg.getResource().location(), arg.getResource().name());

    if (tableData == null) {
      context.out.write("No table data found");
      return;
    }

    if (tableData.getColumnDef().length < 3) {
      context.out.write("Minimum 3 columns are required. latitude, longitude, radius");
      return;
    }

    context.out.write("<script>");
    context.out.writeResource("map/topojson.js");
    context.out.writeResource("map/datamaps.all.js");
    context.out.write("</script>");

    // wordcloud elementId
    String elementId = "map_" + context.getParagraphId();

    // create element
    context.out.write("<div id=\"" + elementId + "\" style=\"height:800px;width:1000px\"></div>");

    // include library
    context.out.write("<script>\n");
    context.out.write("setTimeout(function(){\n");

    // write data
    int numRows = tableData.length();
    String jsonData = "[";
    for (int i = 0; i < numRows; i++) {
      try {
        jsonData += "{ latitude:" + tableData.getData(i, 0).toString() + ", " +
                      "longitude:" + tableData.getData(i, 1).toString() + ", " +
                      "radius: " + tableData.getData(i, 2).toString() + ", " +
                      "fillKey: \"epic\"}";
        if (i != numRows) {
          jsonData += ",";
        }
      } catch (Exception e) {
        continue;
      }
    }
    jsonData += "]";


    context.out.write("var elementId = \"" + elementId + "\";");
    context.out.write("var bubbledata = " + jsonData + ";");
    context.out.writeResource("map/draw.js");

    context.out.write("}, 2000)\n");

    context.out.write("</script>");
  }

  @Override
  public void unload() throws ApplicationException, IOException {

  }

  private static String generateData(int num) throws IOException {
    InputStream ins = ClassLoader.getSystemResourceAsStream("map/mockdata.txt");
    String data = IOUtils.toString(ins);
    return data;
  }


  /**
   * Development mode
   * @param args
   * @throws Exception
   */
  public static void main(String [] args) throws Exception {
    // create development server
    ZeppelinApplicationDevServer dev = new ZeppelinApplicationDevServer(Map.class.getName());

    TableData tableData = new TableData(new InterpreterResult(Code.SUCCESS,
        generateData(40)));

    dev.server.getResourcePool().put("tabledata", tableData);

    // set application argument
    ApplicationArgument arg = new ApplicationArgument(new ResourceKey(
        dev.server.getResourcePoolId(),
        "tabledata"
        ));
    dev.setArgument(arg);

    // start
    dev.server.start();
    dev.server.join();
  }
}