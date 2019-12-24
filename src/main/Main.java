package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import error.*;
import lexicalAnalysis.*;
import syntaxAnalysis.Code;
import syntaxAnalysis.Func;
import syntaxAnalysis.SyntaxAnalysis;
import syntaxAnalysis.Text;

public class Main {

	static String inputPath;
	static String outputPath = "out";
	static FileInputStream fileInputStream;
	static FileOutputStream fileOutputStream;
	static FileOutputStream fileOutputStreamS;
	static ArrayList<Token> tokenList;
	static ArrayList<Func> funcList;

	public static void main(String[] args) {
		int argsLength = args.length;
		// 不提供任何参数时，默认为"-h"
		if (argsLength == 0) {
			h();
		}
		// 一个参数时，只能为"-h"
		else if (argsLength == 1) {
			if (args[0].contentEquals("-h")) {
				h();
			} else {
				Err.error(ErrEnum.CLI_PARA_ERR);
			}
		}
		// 两个参数时，outputPath为默认
		else if (argsLength == 2) {
			inputPath = args[1];
			if (args[0].contentEquals("-s")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.CLI_PARA_ERR);
			}
		}
		// 两个参数时，outputPath为指定路径
		else if (argsLength == 4) {
			inputPath = args[1];
			outputPath = args[3];
			if (args[0].contentEquals("-s") && args[2].contentEquals("-o")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c") && args[2].contentEquals("-o")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.CLI_PARA_ERR);
			}
		} else {
			Err.error(ErrEnum.CLI_PARA_ERR);
		}

	}

	private static void h() {
		System.out.println("Usage:\n" + "  cc0 [options] input [-o file]\n" + "or\n" + "  cc0 [-h]\n" + "options:\n"
				+ "  -s        将输入的c0源代码翻译为文本汇编文件\n" + "  -c        将输入的c0源代码翻译为二进制目标文件\n"
				+ "  -h        显示关于编译器使用的帮助\n" + "  -o file   输出到指定的文件 file\n" + "\n" + "不提供任何参数时，默认为 -h\n"
				+ "提供input不提供-o file时，默认为-o out");
	}

	private static void s(String inputPath, String outputPath) {
		try {
			fileInputStream = new FileInputStream(inputPath);
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.INPUT_FILE_ERR);
		}
		try {
			fileOutputStream = new FileOutputStream(outputPath);
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.OUTPUT_FILE_ERR);
		}
		try {
			fileOutputStreamS = new FileOutputStream(outputPath + ".s");
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.OUTPUT_FILE_ERR);
		}

		LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(fileInputStream);
		tokenList = lexicalAnalysis.lexicalAnalysis();
		viewTokenList(); // 写出tokenList至output.txt
		SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(tokenList);
		// 获得funcList
		funcList = syntaxAnalysis.syntaxAnalysis().getFuncList();
		viewS();

		try {
			fileInputStream.close();
			fileOutputStream.close();
			fileOutputStreamS.close();
		} catch (IOException e) {
			System.err.println("文件流关闭出错");
			System.exit(-1);
		}
	}

	private static void c(String inputPath, String outputPath) {
		System.out.println("将输入的c0源代码翻译为二进制目标文件");
		System.out.println("inputPath：" + inputPath);
		System.out.println("outputPath：" + outputPath);
	}

	private static void viewTokenList() {
		String temp = "Type" + "\t" + "Value" + "\n";
		char[] tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		temp = "";
		for (Token token : tokenList) {
			temp = temp + token.toString();
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
	}

	private static void viewS() {
		String temp;
		char[] tempArr;
		// 常量表表头
		temp = ".constants:" + "\n" + "#" + "\t" + "index" + "\t" + "type" + "\t" + "value" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// 准备常量表正文
		temp = "";
		Integer funcIndex = 0;
		int i;
		for (i = 1; i < funcList.size(); i++) {
			temp = temp + "\t" + funcIndex.toString() + "\t" + "S" + "\t" + funcList.get(i).name + "\n";
			funcIndex = funcIndex + 1;
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}

		Text text;
		ArrayList<Code> codeList;

		// 启动表表头
		temp = ".start:" + "\n" + "#" + "\t" + "index" + "\t" + "op" + "\t" + "on1" + "\t" + "on2" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// 准备启动表正文
		temp = "";
		text = funcList.get(0).text;
		codeList = text.getCodesList();
		for (Code code : codeList) {
			temp = temp + code.toString();
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}

		// 函数表表头
		temp = ".functions:" + "\n" + "#" + "\t" + "index" + "\t" + "_index" + "\t" + "para" + "\t" + "level" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// 准备函数表正文
		temp = "";
		funcIndex = 0;
		for (i = 1; i < funcList.size(); i++) {
			temp = temp + "\t" + funcIndex.toString() + "\t" + funcIndex.toString() + "\t"
					+ funcList.get(i).paraNum.toString() + "\t" + "1" + "\n";
			funcIndex = funcIndex + 1;
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}

		// 函数体表头
		temp = "#" + "\t" + "index" + "\t" + "op" + "\t" + "on1" + "\t" + "on2" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStreamS.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		temp = "";
		funcIndex = 0;
		for (i = 1; i < funcList.size(); i++) {
			codeList = funcList.get(i).text.getCodesList();
			// 函数头
			temp = temp + ".F" + funcIndex.toString() + ":" + "\n";
			for (Code code : codeList) {
				temp = temp + code.toString();
			}

			tempArr = temp.toCharArray();
			try {
				for (char ch : tempArr) {
					fileOutputStreamS.write(ch);
				}
			} catch (Exception e) {
				Err.error(ErrEnum.OUTPUT_ERR);
			}
			funcIndex = funcIndex + 1;
			temp = "";
		}
	}
}
