import java.util.*;
 
import javax.naming.*;
import javax.naming.directory.*;
 
/**
 * 
 * Modified version of: https://venkatsadasivam.com/2014/01/03/java-sample-active-directory-authentication-code/
 *
 */
public final class ActiveDirectoryAuthenticate {
 
    private final String MASTER_USER_DN;
    private final String MASTER_PASSWORD;
 
    private final String ldapUrl = "ldap://54.149.247.168:389";
    private final String searchBase = "OU=ChatUsers,DC=campusletters,DC=org";

    public ActiveDirectoryAuthenticate(String username, String password) 
    {
    	this.MASTER_USER_DN = "CL\\" + username;
    	this.MASTER_PASSWORD = password;
    }
    /*
    public boolean hasGroup(String username, String password, String groupObjectName) throws NamingException 
    {
        List<String> allGroups = getAllGroups(username, password);
        return allGroups.contains(groupObjectName);
    }
    */
    private String getFirstName(DirContext ctxs, String username) throws NamingException
    {    	 
        DirContext ctx = ctxs;
 
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[] {"givenname"});
        NamingEnumeration<SearchResult> searchResults = ctx.search(searchBase,String.format("(sAMAccountName=%s)", username), searchControls);
        if (!searchResults.hasMore()) 
        {
            throw new NamingException();
        }
        SearchResult searchResult = searchResults.next();
        Attributes attributes = searchResult.getAttributes();
        Attribute attribute = attributes.get("givenname");
        return (String) attribute.get();
    }
    /*
    private List<String> getAllGroups(String username, String password) throws NamingException 
    {
        List<String> result = new ArrayList<>();
 
        String attributeToLookup = "memberOf";
 
        DirContext ctx = authenticate();
 
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[] {"distinguishedName"});
        NamingEnumeration<SearchResult> searchResults = ctx.search(searchBase,String.format("(sAMAccountName=%s)", username), searchControls);
        if (!searchResults.hasMore()) 
        {
            throw new NamingException();
        }
        SearchResult searchResult = searchResults.next();
        Attributes attributes = searchResult.getAttributes();
        Attribute attribute = attributes.get("distinguishedName");
        String userObject = (String) attribute.get();
 
        ctx.close();
        ctx = authenticate();
 
        attributes = ctx.getAttributes(userObject, new String[] {attributeToLookup});
 
        NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
        while (allAttributes.hasMoreElements()) 
        {
            attribute = allAttributes.nextElement();
            int size = attribute.size();
            for (int i = 0; i < size; i++) 
            {
                String attributeValue = (String) attribute.get(i);
                result.add(attributeValue);
            }
        }
 
        ctx.close();
 
        return result;
    }
 	*/
    public String authenticate() throws NamingException 
    {
        String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        String securityAuthentication = "simple";
 
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_PRINCIPAL, MASTER_USER_DN);
        env.put(Context.SECURITY_CREDENTIALS, MASTER_PASSWORD);

        DirContext ctx = new InitialDirContext(env);
        return getFirstName(ctx, this.MASTER_USER_DN.substring(3));
    }
}