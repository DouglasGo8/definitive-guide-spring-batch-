package com.apress.springbatch.multi.resource.batch;

import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author dbatista
 */
@Component
public class CustomerXmlHeaderCallback implements StaxWriterCallback {

    @Override
    public void write(XMLEventWriter xmlEventWriter) throws IOException {

        final XMLEventFactory factory = XMLEventFactory.newInstance();

        try {
            xmlEventWriter.add(factory.createStartElement("", "", "identification"));
            xmlEventWriter.add(factory.createStartElement("", "", "author"));
            xmlEventWriter.add(factory.createAttribute("name", "Michael Minella"));
            xmlEventWriter.add(factory.createEndElement("", "", "author"));
            xmlEventWriter.add(factory.createEndElement("", "", "identification"));
        } catch (XMLStreamException e) {
            System.err.println("An error occured: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
