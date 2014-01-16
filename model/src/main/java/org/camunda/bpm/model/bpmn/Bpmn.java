/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.model.bpmn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.camunda.bpm.model.bpmn.impl.*;
import org.camunda.bpm.model.core.Model;
import org.camunda.bpm.model.core.ModelBuilder;
import org.camunda.bpm.model.core.ModelException;
import org.camunda.bpm.model.core.ModelParseException;
import org.camunda.bpm.model.core.ModelValidationException;
import org.camunda.bpm.model.core.impl.instance.EventDefinitionRefImpl;
import org.camunda.bpm.model.core.impl.util.IoUtil;

/**
 * <p>Provides access to the camunda BPMN model api.</p>
 *
 * @author Daniel Meyer
 *
 */
public class Bpmn {

  /** the singleton instance of {@link Bpmn}. If you want to customize the behavior of Bpmn,
   * replace this instance with an instance of a custom subclass of {@link Bpmn}. */
  public final static Bpmn INSTANCE = new Bpmn();

  /** the parser used by the Bpmn implementation. */
  protected final BpmnParser bpmnParser = new BpmnParser();

  /** The {@link Model}
   */
  protected Model bpmnModel;

  /**
   * Allows reading a {@link BpmnModelInstance} from a File.
   *
   * @param file the {@link File} to read the {@link BpmnModelInstance} from
   * @return the model read
   * @throws BpmnModelException if the model cannot be read
   */
  public static BpmnModelInstance readModelFromFile(File file) {
    return INSTANCE.doReadModelFromFile(file);
  }

  /**
   * Allows reading a {@link BpmnModelInstance} from an {@link InputStream}
   *
   * @param stream the {@link InputStream} to read the {@link BpmnModelInstance} from
   * @return the model read
   * @throws ModelParseException if the model cannot be read
   */
  public static BpmnModelInstance readModelFromStream(InputStream stream) {
    return INSTANCE.doReadModelFromInputStream(stream);
  }

  /**
   * Allows writing a {@link BpmnModelInstanceImpl} to a File. It will be
   * validated before writing.
   *
   * @param file the {@link File} to write the {@link BpmnModelInstanceImpl} to
   * @param model the {@link BpmnModelInstanceImpl} to write
   * @throws BpmnModelException if the model cannot be written
   * @throws ModelValidationException if the model is not valid
   */
  public static void writeModelToFile(File file, BpmnModelInstanceImpl model) {
    INSTANCE.doWriteModelToFile(file, model);
  }

  /**
   * Allows writing a {@link BpmnModelInstanceImpl} to an {@link OutputStream}. It will be
   * validated before writing.
   *
   * @param stream the {@link OutputStream} to write the {@link BpmnModelInstanceImpl} to
   * @param model the {@link BpmnModelInstanceImpl} to write
   * @throws ModelException if the model cannot be written
   * @throws ModelValidationException if the model is not valid
   */
  public static void writeModelToStream(OutputStream stream, BpmnModelInstanceImpl model) {
    INSTANCE.doWriteModelToOutputStream(stream, model);
  }

  /**
   * Validate model DOM document
   *
   * @param model the {@link BpmnModelInstanceImpl} to validate
   * @throws ModelValidationException if the model is not valid
   */
  public static void validateModel(BpmnModelInstanceImpl model) {
    INSTANCE.doValidateModel(model);
  }

  /**
   * Allows creating an new, empty {@link BpmnModelInstance}.
   *
   * @return the empty model.
   */
  public static BpmnModelInstance createEmptyModel() {
    return INSTANCE.doCreateEmptyModel();
  }

  /**
   * Register known types of the BPMN model
   */
  public Bpmn() {
    ModelBuilder bpmnModelBuilder = ModelBuilder.createInstance("BPMN Model");
    doRegisterTypes(bpmnModelBuilder);
    bpmnModel = bpmnModelBuilder.build();
  }

  protected BpmnModelInstance doReadModelFromFile(File file) {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return doReadModelFromInputStream(is);

    } catch (FileNotFoundException e) {
      throw new BpmnModelException("Cannot read model from file "+file+": file does not exist.");

    } finally {
      IoUtil.closeSilently(is);

    }
  }

  protected BpmnModelInstance doReadModelFromInputStream(InputStream is) {
    return bpmnParser.parseModelFromStream(is);
  }

  protected void doWriteModelToFile(File file, BpmnModelInstanceImpl model) {
    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      doWriteModelToOutputStream(os, model);
    }
    catch (FileNotFoundException e) {
      throw new BpmnModelException("Cannot read model from file "+file+": file does not exist.");
    } finally {
      IoUtil.closeSilently(os);
    }
  }

  protected void doWriteModelToOutputStream(OutputStream os, BpmnModelInstanceImpl model) {
    // validate DOM document
    doValidateModel(model);
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource domSource = new DOMSource(model.getDocument());
      StreamResult result = new StreamResult(os);
      transformer.transform(domSource, result);
    } catch (TransformerConfigurationException e) {
      throw new BpmnModelException("Cannot create transformer to write the model", e);
    } catch (TransformerException e) {
      throw new BpmnModelException("Cannot transform model to xml", e);
    }
  }

  protected void doValidateModel(BpmnModelInstanceImpl model) {
    bpmnParser.validateModel(model.getDocument());
  }

  protected BpmnModelInstance doCreateEmptyModel() {
    return bpmnParser.getEmptyModel();
  }

  protected void doRegisterTypes(ModelBuilder bpmnModelBuilder) {
    EventDefinitionRefImpl.registerType(bpmnModelBuilder);
    DefinitionsImpl.registerType(bpmnModelBuilder);
    ImportImpl.registerType(bpmnModelBuilder);
    BaseElementImp.registerType(bpmnModelBuilder);
    PropertyImpl.registerType(bpmnModelBuilder);
    RootElementImpl.registerType(bpmnModelBuilder);
    ExtensionElementsImpl.registerType(bpmnModelBuilder);
    CallableElementImpl.registerType(bpmnModelBuilder);
    ProcessImpl.registerType(bpmnModelBuilder);
    EventDefinitionImpl.registerType(bpmnModelBuilder);
    MessageImpl.registerType(bpmnModelBuilder);
    MessageEventDefinitionImpl.registerType(bpmnModelBuilder);
    FlowElementImpl.registerType(bpmnModelBuilder);
    FlowNodeImpl.registerType(bpmnModelBuilder);
    EventImpl.registerType(bpmnModelBuilder);
    CatchEventImpl.registerType(bpmnModelBuilder);
    StartEventImpl.registerType(bpmnModelBuilder);
  }

  /**
   * @return the {@link Model} instance to use
   */
  public Model getBpmnModel() {
    return bpmnModel;
  }

  /**
   * @param bpmnModel the bpmnModel to set
   */
  public void setBpmnModel(Model bpmnModel) {
    this.bpmnModel = bpmnModel;
  }

}
