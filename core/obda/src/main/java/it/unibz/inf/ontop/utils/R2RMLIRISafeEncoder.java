package it.unibz.inf.ontop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class R2RMLIRISafeEncoder {

    private static final Logger log = LoggerFactory.getLogger(R2RMLIRISafeEncoder.class);

    /**
     * This table is used for IRI safe encoding according to
     * <p>
     * <a href="https://www.w3.org/TR/r2rml/">R2RML</a>
     * <p>
     * The IRI-safe version of a string is obtained by applying the following transformation to any character that is not in the iunreserved production in [RFC3987]:
     * <ul>
     * <li>Convert the character to a sequence of one or more octets using UTF-8 [RFC3629]</li>
     * <li>Percent-encode each octet [RFC3986]</li>
     * </ul>
     * <p>
     * <p>
     * <a href="https://tools.ietf.org/html/rfc3987">RFC 3987</a> 2.2.  ABNF for IRI References and IRIs
     * <pre>
     * iunreserved = ALPHA / DIGIT / "-" / "." / "_" / "~" / ucschar
     * ucschar  = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF
     *            / %x10000-1FFFD / %x20000-2FFFD / %x30000-3FFFD
     *            / %x40000-4FFFD / %x50000-5FFFD / %x60000-6FFFD
     *            / %x70000-7FFFD / %x80000-8FFFD / %x90000-9FFFD
     *            / %xA0000-AFFFD / %xB0000-BFFFD / %xC0000-CFFFD
     *            / %xD0000-DFFFD / %xE1000-EFFFD
     * </pre>
     * <p>
     * <p>
     *
     * We only implement the encoding for the range of basic latin (\u0020 - \u007F) for performance reason.
     *  Other symbols outside of `iunreserved` are mostly control symbols.
     *
     *  NB: it is important to place "%" to be beginning if we implement the encoding by a sequence of replacements.
     */
    public static final ImmutableMap<String, String> TABLE = ImmutableMap.<String, String>builder()
            .put("%25", "%")
            .put("%20", " ")
            .put("%21", "!")
            .put("%22", "\"") // .put("%22", "''")
            .put("%23", "#")
            .put("%24", "$")
            .put("%26", "&")
            .put("%27", "'")
            .put("%28", "(")
            .put("%29", ")")
            .put("%2A", "*")
            .put("%2B", "+")
            .put("%2C", ",")
            // "%2D", "-"  iunreserved
            // "%2E", "."  iunreserved
            .put("%2F", "/")
            // "0" - "9"
            .put("%3A", ":")
            .put("%3B", ";")
            .put("%3C", "<")
            .put("%3D", "=")
            .put("%3E", ">")
            .put("%3F", "?")
            .put("%40", "@")
            // "A" - "Z"
            .put("%5B", "[")
            .put("%5C", "\\")
            .put("%5D", "]")
            .put("%5E", "^")
            // "%5F", "_"  iunreserved
            .put("%60", "`")
            // "a" - "z"
            .put("%7B", "{")
            .put("%7C", "|")
            .put("%7D", "}")
            // "%7E", "~"  iunreserved
            // .put("%7F", "\u007F") // DEL
            .build();


    /*
     * percent encoding for a String
     */
    public static String encode(String pe) {
        for (Map.Entry<String, String> e : TABLE.entrySet())
            //if (!e.getKey().equals("%22"))
            pe = pe.replace(e.getValue(), e.getKey());

        return pe;
    }

    /***
     * Given a string representing a URI, this method will return a new String
     * in which all percent encoded characters (e.g., %20) will
     * be restored to their original characters (e.g., ' ').
     * This is necessary to transform some URIs into the original database values.
     *
     * @param encodedURI
     * @return
     */

    public static String decode(String encodedURI) {

        int length = encodedURI.length();
        StringBuilder strBuilder = new StringBuilder(length + 20);
        char[] codeBuffer = new char[3];

        for (int i = 0; i < length; i++) {
            char c = encodedURI.charAt(i);

            if (c != '%') {
                // base case, the character is a normal character, just append
                strBuilder.append(c);
                continue;
            }

            // found a escape, processing the code and replacing it by
            // the original value that should be found on the DB. This
            // should not be used all the time, only when working in
            // virtual mode... we need to fix this with a FLAG.
            // First we get the 2 chars next to %
            codeBuffer[0] = '%';
            codeBuffer[1] = encodedURI.charAt(i + 1);
            codeBuffer[2] = encodedURI.charAt(i + 2);

            // now we check if they match any of our escape codes, if
            // they do the char to be inserted is put in codeBuffer otherwise
            String code = String.copyValueOf(codeBuffer);
            String rep = TABLE.get(code);
            if (rep != null)
                strBuilder.append(rep);
            else {
                // This was not an escape code, so we just append the characters and continue;
                log.warn("Error decoding an encoded URI from the query. Problematic code: {}\nProblematic URI: {}", code, encodedURI);
                strBuilder.append(codeBuffer);
            }
            i += 2;
        }
        return strBuilder.toString();
    }


}
