---------------------------------------------------------------------------
Standard Tag Library 1.1 -- BINARY DISTRIBUTION
---------------------------------------------------------------------------
Thanks for downloading this release of the Standard tag library, 
an implementation of the JavaServer Pages(tm)(JSP) 
Standard Tag Library (JSTL).

JSTL is an effort of the Java Community Process (JCP) and
comes out of the JSR-052 expert group. For more information on JSTL,
please go to http://java.sun.com/products/jstl.

We hope you find the tags, documents, and examples in this binary
distribution of interest.

---------------------------------------------------------------------------
LIBRARY DEPENDENCIES

This version of the Standard Tag Library has the following runtime
dependencies:

   1. Dependencies provided by the JSP 2.0 container:
      - Servlet 2.4
      - JSP 2.0

   2. Dependencies provided in newer J2SEs (1.4.2 and higher)
      - JAXP 1.2 
      - Xalan 2.5 
      - JDBC Standard Extension 2.0

Since all of the dependencies in (2) are included in Sun's
distribution of J2SE 1.4.2 (and higher), this is therefore the J2SE
version of choice to use the standard tag library.

If the java platform under which you run your JSP container does not
provide these dependencies, they must be made available either globally
to all web-applications by your container, or individually within the
WEB-INF/lib directory of your web-application.

For convenience, these jar files have been included in directory 
lib/old-dependencies of this distribution (assuming the build process
of this distribution included them). If you would like to download
these jar files yourself, instructions on where you can get them are 
included below.

---
JAXP 1.2

The JAXP 1.2 jar files can be obtained in the Java Web Services
Developer Pack (JWSDP) available at 
http://java.sun.com/products/jwsdp.

  - jaxp-api.jar
  - dom.jar
  - sax.jar
  - xercesImpl.jar

---
Xalan 2.5

The Xalan jar file can be obtained in the Java Web Services
Developer Pack (JWSDP) available at 
http://java.sun.com/products/jwsdp, as well as from 
Apache at http://xml.apache.org/xalan-j.

  - xalan.jar

Please note that if you use Sun's distribution of J2SE 1.4.1, you must
supersede the version of xalan.jar provided by the J2SE with version
2.5 or higher of Xalan.  This newer version of xalan.jar must then be
made available through the endorsed dirs mechanism.

---
JDBC Standard Extension 2.0

The JDBC 2.0 Optional Package can be obtained from:
http://java.sun.com/products/jdbc/download.html

  - jdbc2_0-stdext.jar

---------------------------------------------------------------------------
WAR Files

The following two files are standalone web applications (WARs) that are
designed to work out of the box in order to help you learn JSTL:

   standard-doc.war                Documentation
   standard-examples.war           Simple examples of JSTL tags

Note that 'standard-examples.war' will work out-of-the-box as long 
as the java platform under which you run your JSP container provides
all the dependencies mentioned above (see Library Dependencies). 
This is the case if using Sun's distribution of J2SE 1.4.2 (and higher).

If not all dependencies are provided by your runtime, then they must 
be made available to the web application as explained in section
"Library Dependencies".

---------------------------------------------------------------------------
USING THE STANDARD TAG LIBRARY

To use this distribution with your own web applications, simply copy the JAR
files in the 'lib' directory (jstl.jar and standard.jar) to your application's 
WEB-INF/lib directory (add the other dependencies as well if your runtime
does not already provide them). Then, import JSTL into your pages with the 
following directives:

  CORE LIBRARY
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  XML LIBRARY
    <%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

  FMT LIBRARY 
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

  SQL LIBRARY
    <%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

  FUNCTIONS LIBRARY
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

---------------------------------------------------------------------------
COMPATIBILITY

The 1.1 version of the Standard Taglib has been tested under Tomcat 5.0.3
and should work in any compliant JSP 2.0 container.

---------------------------------------------------------------------------
COMMENTS AND QUESTIONS

Please join the taglibs-user@jakarta.apache.org mailing list if you have
general usage questions about JSTL.

Comments about the JSTL specification itself should be sent to
jsr-52-comments@jcp.org.

Enjoy!

