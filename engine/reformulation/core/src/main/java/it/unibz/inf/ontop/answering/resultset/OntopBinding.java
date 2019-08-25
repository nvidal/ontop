package it.unibz.inf.ontop.answering.resultset;

import it.unibz.inf.ontop.exception.OntopResultConversionException;
import it.unibz.inf.ontop.model.term.Constant;
import org.eclipse.rdf4j.query.Binding;

public interface OntopBinding {
    /**
     * Gets the name of the binding (e.g. the variable name).
     *
     * @return The name of the binding.
     */
    String getName();

    /**
     * Gets the value of the binding. The returned value is never equal to <tt>null</tt>, such a "binding" is
     * considered to be unbound.
     *
     * @return The value of the binding, never <tt>null</tt>.
     */
    Constant getValue() throws OntopResultConversionException;
}
