package edu.upvictoria.fpoo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WHERE {
    private Map<String, Integer> columnIndexMap;

    public WHERE(Map<String, Integer> columnIndexMap) {
        this.columnIndexMap = columnIndexMap;
    }

    public boolean evaluateCondition(String[] row, String condition) {
        List<String> tokens = tokenize(normalizeCondition(condition));
        return evaluateTokens(row, tokens);
    }

    private String normalizeCondition(String condition) {
        return condition.trim()
                .replaceAll("\\s*(=|!=|<=|>=|<|>)\\s*", " $1 ")
                .replaceAll("\\s+(AND|OR)\\s+", " $1 ")
                .replaceAll("\\s*\\(\\s*", "(")
                .replaceAll("\\s*\\)\\s*", ")");
    }

    private List<String> tokenize(String condition) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\w+|=|!=|<=|>=|<|>|\\(|\\)|AND|OR|\\'[^\\']*\\'|\"[^\"]*\"").matcher(condition);
        while (matcher.find()) {
            tokens.add(matcher.group().trim());
        }
        return tokens;
    }

    private boolean evaluateTokens(String[] row, List<String> tokens) {
        Deque<Boolean> values = new ArrayDeque<>();
        Deque<String> operators = new ArrayDeque<>();

        int i = 0;
        while (i < tokens.size()) {
            String token = tokens.get(i);
            if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    applyOperator(values, operators.pop());
                }
                operators.pop(); // Eliminar '('
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                    applyOperator(values, operators.pop());
                }
                operators.push(token);
            } else {
                StringBuilder condition = new StringBuilder(token);
                while (i + 1 < tokens.size() && !isOperator(tokens.get(i + 1)) && !tokens.get(i + 1).equals("(") && !tokens.get(i + 1).equals(")")) {
                    condition.append(" ").append(tokens.get(++i));
                }
                values.push(evaluateSimpleCondition(row, condition.toString()));
            }
            i++;
        }

        while (!operators.isEmpty()) {
            applyOperator(values, operators.pop());
        }

        return values.pop();
    }

    private boolean evaluateSimpleCondition(String[] row, String condition) {
        Pattern pattern = Pattern.compile("(\\w+)\\s*(=|!=|<=|>=|<|>)\\s*(.*)");
        Matcher matcher = pattern.matcher(condition);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Formato de condición inválido: " + condition);
        }

        String column = matcher.group(1).trim().toUpperCase();
        String operator = matcher.group(2).trim();
        String value = matcher.group(3).trim();

        value = value.replaceAll("^['\"]|['\"]$", "");

        if (!columnIndexMap.containsKey(column)) {
            System.out.println("Error: Columna no encontrada: " + column);
            return false;
        }

        int colIndex = columnIndexMap.get(column);
        String cellValue = row[colIndex].trim();
        return compare(cellValue, operator, value);
    }

    private boolean compare(String cellValue, String operator, String value) {
        // Elimina las comillas simples o dobles del inicio y fin de los valores
        cellValue = cellValue.replaceAll("^['\"]|['\"]$", "").toLowerCase();
        value = value.replaceAll("^['\"]|['\"]$", "").toLowerCase();
    
        try {
            double cellValueNum = Double.parseDouble(cellValue);
            double valueNum = Double.parseDouble(value);
            switch (operator) {
                case "=":
                    return cellValueNum == valueNum;
                case "!=":
                    return cellValueNum != valueNum;
                case "<":
                    return cellValueNum < valueNum;
                case ">":
                    return cellValueNum > valueNum;
                case "<=":
                    return cellValueNum <= valueNum;
                case ">=":
                    return cellValueNum >= valueNum;
                default:
                    throw new IllegalArgumentException("Operador desconocido: " + operator);
            }
        } catch (NumberFormatException e) {
            switch (operator) {
                case "=":
                    return cellValue.equals(value);
                case "!=":
                    return !cellValue.equals(value);
                default:
                    throw new IllegalArgumentException("Cannot compare non-numeric values with operator: " + operator);
            }
        }
    }

    private void applyOperator(Deque<Boolean> values, String operator) {
        boolean right = values.pop();
        boolean left = values.pop();
        boolean result;
        switch (operator.toUpperCase()) {
            case "AND":
                result = left && right;
                break;
            case "OR":
                result = left || right;
                break;
            default:
                throw new IllegalArgumentException("Operador Desconocido: " + operator);
        }
        values.push(result);
    }

    private int precedence(String operator) {
        switch (operator.toUpperCase()) {
            case "AND":
                return 2;
            case "OR":
                return 1;
            default:
                return 0;
        }
    }

    private boolean isOperator(String token) {
        token=token.toUpperCase();
        return token.equals("AND") || token.equals("OR");
    }

}