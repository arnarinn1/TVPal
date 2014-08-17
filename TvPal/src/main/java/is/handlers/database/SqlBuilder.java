package is.handlers.database;

import java.util.Iterator;
import java.util.List;

public class SqlBuilder
{
    private StringBuilder builder;

    public SqlBuilder()
    {
        this.builder = new StringBuilder();
    }

    public SqlBuilder AddSelectColumns(List<String> columns)
    {
        if(columns.size() < 1) throw new IllegalArgumentException("Must add more than 1 argument");

        Iterator<String> iterator = columns.listIterator();

        builder.append(String.format("select %s as _id", iterator.next()));

        while(iterator.hasNext())
            builder.append(String.format(",%s ", iterator.next()));

        return this;
    }

    public SqlBuilder AddTable(String tableName)
    {
        builder.append("from ").append(tableName).append(" ");

        return this;
    }

    public SqlBuilder AddOrderByColumn(String column, boolean desc)
    {
        builder.append(SqlStatements.OrderBy).append(column).append(" ");

        if (desc) builder.append("desc ");

        return this;
    }

    public SqlBuilder AddWhereClause(String column, String equals, String condition)
    {
        builder.append("where ").append(column).append(condition).append(equals).append(" ");

        return this;
    }

    public SqlBuilder AddWhereAndClause(String column, String equals, String condition)
    {
        builder.append("and ").append(column).append(condition).append(equals).append(" ");

        return this;
    }

    public String Build()
    {
        try
        {
            return builder.toString();
        }
        finally
        {
            builder = new StringBuilder();
        }
    }

    public interface SqlStatements
    {
        String OrderBy = "order by ";
    }

    public interface Comparison
    {
        String Equals = " = ";
    }
}