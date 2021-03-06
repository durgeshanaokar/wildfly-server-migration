/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.migration.wfly10.config.task.management.extension;

import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.migration.core.task.ServerMigrationTaskResult;
import org.jboss.migration.wfly10.config.management.ManageableServerConfiguration;
import org.jboss.migration.wfly10.config.task.management.configuration.ManageableServerConfigurationLeafTask;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MODULE;

/**
 * Builder for a task which creates an extension if its missing from the server's config.
 * @author emmartins
 */
public class AddExtensionTaskBuilder<S> extends ManageableServerConfigurationLeafTask.Builder<S> {
    public AddExtensionTaskBuilder(String extensionModule) {
        name("extension."+extensionModule+".add");
        runBuilder(params -> context -> {
            final ManageableServerConfiguration extensionResourceParent = params.getServerConfiguration();
            if (!extensionResourceParent.getExtensionResourceNames().contains(extensionModule)) {
                context.getLogger().debugf("Adding Extension %s...", extensionModule);
                final ModelNode op = Util.createAddOperation(extensionResourceParent.getExtensionResourcePathAddress(extensionModule));
                op.get(MODULE).set(extensionModule);
                extensionResourceParent.getServerConfiguration().executeManagementOperation(op);
                context.getLogger().infof("Extension %s added.",extensionModule);
                return ServerMigrationTaskResult.SUCCESS;
            } else {
                context.getLogger().infof("Extension %s already exists in config.", extensionModule);
                return ServerMigrationTaskResult.SKIPPED;
            }
        });
    }
}
