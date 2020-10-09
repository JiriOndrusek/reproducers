/**
 * Copyright (C) 2012 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.goots.groovy

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.commonjava.maven.atlas.ident.ref.SimpleProjectRef
import org.commonjava.maven.ext.core.groovy.BaseScript
import org.commonjava.maven.ext.core.groovy.InvocationPoint
import org.commonjava.maven.ext.core.groovy.InvocationStage
import org.commonjava.maven.ext.core.groovy.PMEBaseScript

@InvocationPoint(invocationPoint = InvocationStage.LAST)
@PMEBaseScript BaseScript pme

println "#### pmeAlterReadme START"

String version = pme.getProject().getModel().getProperties().getProperty('fuse.bom.version')

if ((m = version =~/(\d+)\.(\d+)\..*/)) {
    def firstVersion = m.group(1)
    def secondVersion = m.group(2)

    //version 7.x has tag of image 1:x
    String fuseImage = 'fuse-java-openshift:1.' + secondVersion
    ing fuseImageRepository = 'registry.redhat.io/fuse' + firstVersion + '/' + fuseImage
    String name = pme.getProject().getModel().getName();
    println("For quickstart '" + name + "' replace version for import-image or documentation to version " + firstVersion + '.' + secondVersion + '. Image: ' + fuseImage)

    String fileName = "README.adoc"
    new File(fileName + ".bak").withWriter { w ->
        new File(fileName).eachLine { line ->
            w << line.replaceAll('oc import-image .*', 'oc import image ' + fuseImage + ' --from=' + fuseImageRepository + ' --confirm')
                    .replaceAll('red_hat_fuse/\\d+.\\d+/html/fuse_on_openshift_guide', 'red_hat_fuse/' + firstVersion + '.' + secondVersion + '/html/fuse_on_openshift_guide') 
		    .replaceAll('mvn clean -DskipTests fabric8:deploy -Popenshift -Dfabric8.generator.fromMode=istag -Dfabric8.generator.from=MY_PROJECT_NAME/.*', 
				'mvn clean -DskipTests fabric8:deploy -Popenshift -Dfabric8.generator.fromMode=istag -Dfabric8.generator.from=MY_PROJECT_NAME/' + fuseImage) + System.getProperty("line.separator")
        }
    }
    Files.copy(Paths.get(fileName + ".bak"), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING)
    new File(fileName + ".bak").delete()
}

println "#### pmeAlterReadme END"

pme.inlineProperty (pme.getProject(), SimpleProjectRef.parse("org.apache.maven:maven-core"))
