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
package org.camunda.bpm.model.core.testmodel;

import static org.camunda.bpm.model.core.testmodel.TestModelConstants.*;

import org.camunda.bpm.model.core.ModelBuilder;
import org.camunda.bpm.model.core.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.core.type.ModelElementTypeBuilder;

/**
 * @author Daniel Meyer
 *
 */
class FlyingAnimal extends Animal {

  static void registerType(ModelBuilder modelBuilder) {

    ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(FlyingAnimal.class, TYPE_NAME_FLYING_ANIMAL)
      .namespaceUri(MODEL_NAMESPACE)
      .extendsType(Animal.class)
      .abstractType();

    typeBuilder.build();

  }

  FlyingAnimal(ModelTypeInstanceContext instanceContext) {
    super(instanceContext);
  }

}
