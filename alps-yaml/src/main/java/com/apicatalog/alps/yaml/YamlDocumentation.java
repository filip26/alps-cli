/*
 * Copyright 2020 the original author or authors.
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
package com.apicatalog.alps.yaml;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.apicatalog.alps.dom.element.Documentation;
import com.apicatalog.yaml.Yaml;
import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlMappingBuilder;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlSequence;
import com.apicatalog.yaml.YamlSequenceBuilder;

final class YamlDocumentation implements Documentation {

    private URI href;
    private Content content;
    
    @Override
    public Optional<URI> href() {
        return Optional.ofNullable(href);
    }

    @Override
    public Optional<Content> content() {
        return Optional.ofNullable(content);
    }
    
    public static final Optional<YamlNode> toYaml(final Set<Documentation> documentation, final boolean verbose) {
        
        if (documentation == null || documentation.isEmpty()) {
            return Optional.empty();
        }
        
        if (documentation.size() == 1) {
            return toYaml(documentation.iterator().next(), verbose);
        }
        
        final YamlSequenceBuilder yamlDocs = Yaml.createSequenceBuilder();
        
        documentation.stream().map(d -> YamlDocumentation.toYaml(d, verbose)).flatMap(Optional::stream).forEach(yamlDocs::add);
        
        final YamlSequence array = yamlDocs.build();
        
        return array.isEmpty() ? Optional.empty() : Optional.of(array);
    }

    public static final Optional<YamlNode> toYaml(final Documentation documentation, final boolean verbose) {
        
        if (documentation == null || (documentation.href().isEmpty() && documentation.content().isEmpty())) {
            return Optional.empty();
        }
        
        final Optional<Content> content = documentation.content();
        
        if (documentation.href().isEmpty()
                && content.isPresent()
                && content
                       .map(Documentation.Content::type)
                       .filter(Predicate.isEqual(YamlConstants.MEDIA_TYPE_TEXT_PLAIN).or(Predicate.isEqual("text")))
                       .isPresent()
                ) {

            return Optional.of(Yaml.createScalar(content.get().value()));            
        }
                     
        final YamlMappingBuilder doc = Yaml.createMappingBuilder();
        
        documentation.href().ifPresent(href -> doc.add(YamlConstants.HREF, Yaml.createScalar(href.toString())));

        if (verbose) {
            content
                .map(Documentation.Content::type)
                .ifPresentOrElse(
                        t -> doc.add(YamlConstants.FORMAT, Yaml.createScalar(t)), 
                        () -> doc.add(YamlConstants.FORMAT, Yaml.createScalar("text"))
                        );
        } else {
            content
                .map(Documentation.Content::type)
                .filter(Predicate.isEqual(YamlConstants.MEDIA_TYPE_TEXT_PLAIN).negate().and(Predicate.isEqual("text").negate()))
                .ifPresent(type -> doc.add(YamlConstants.FORMAT, Yaml.createScalar(type)));            
        }
    
        content
            .map(Documentation.Content::value)
            .ifPresent(value -> doc.add(YamlConstants.VALUE, Yaml.createScalar(value)));
        
        final YamlMapping yamlDoc = doc.build();
        
        return yamlDoc.isEmpty() ? Optional.empty() : Optional.of(yamlDoc);
    }
    
    class YamlContent implements Content {
        
        private String type;
        private String value;
        
        @Override
        public String type() {
            return type;
        }

        @Override
        public String value() {
            return value;
        }
    }
    
}
