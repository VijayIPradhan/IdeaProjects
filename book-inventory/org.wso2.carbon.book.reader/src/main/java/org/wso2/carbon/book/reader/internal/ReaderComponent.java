
package org.wso2.carbon.book.reader.internal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.book.provider.BookProvider;
import org.wso2.carbon.book.reader.Reader;

import java.util.logging.Logger;

@Component(
        name="org.wso2.carbon.book.reader",
        immediate = true
)

public class ReaderComponent {
    private static final Logger LOGGER = Logger.getLogger(ReaderComponent.class.getName());

    @Activate
    protected void activate(ComponentContext context){
        LOGGER.info("Reader bundle is activated");
        Reader reader = new Reader();
        reader.getBookCreated("Harry Potter", "JK Rowling", "112112");
    }

    @Reference(
            name="org.wso2.carbon.book.provider",
            service=BookProvider.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetBookProvider"
    )
    protected void setBookProvider(BookProvider bookProvider) {

        LOGGER.info("Book provider is set to Reader bundle.");
        ReaderDataHolder.getInstance().setBookProvider(bookProvider);
    }

    protected void unsetBookProvider(BookProvider bookProvider) {

        LOGGER.info("Book privider is unset to Reader bundle.");
        ReaderDataHolder.getInstance().setBookProvider(null);
    }
}
