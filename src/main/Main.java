package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import error.*;
import lexicalAnalysis.*;
import syntaxAnalysis.SyntaxAnalysis;

public class Main {

	static String inputPath;
	static String outputPath = "out";
	static FileInputStream fileInputStream;
	static FileOutputStream fileOutputStream;
	static ArrayList<Token> tokenList;

	public static void main(String[] args) {
		int argsLength = args.length;
		// ���ṩ�κβ���ʱ��Ĭ��Ϊ"-h"
		if (argsLength == 0) {
			h();
		}
		// һ������ʱ��ֻ��Ϊ"-h"
		else if (argsLength == 1) {
			if (args[0].contentEquals("-h")) {
				h();
			} else {
				Err.error(ErrEnum.PARA_ERR);
			}
		}
		// ��������ʱ��outputPathΪĬ��
		else if (argsLength == 2) {
			inputPath = args[1];
			if (args[0].contentEquals("-s")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.PARA_ERR);
			}
		}
		// ��������ʱ��outputPathΪָ��·��
		else if (argsLength == 4) {
			inputPath = args[1];
			outputPath = args[3];
			if (args[0].contentEquals("-s") && args[2].contentEquals("-o")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c") && args[2].contentEquals("-o")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.PARA_ERR);
			}
		} else {
			Err.error(ErrEnum.PARA_ERR);
		}

	}

	private static void h() {
		System.out.println("Usage:\n" + "  cc0 [options] input [-o file]\n" + "or\n" + "  cc0 [-h]\n" + "options:\n"
				+ "  -s        �������c0Դ���뷭��Ϊ�ı�����ļ�\n" + "  -c        �������c0Դ���뷭��Ϊ������Ŀ���ļ�\n"
				+ "  -h        ��ʾ���ڱ�����ʹ�õİ���\n" + "  -o file   �����ָ�����ļ� file\n" + "\n" + "���ṩ�κβ���ʱ��Ĭ��Ϊ -h\n"
				+ "�ṩinput���ṩ-o fileʱ��Ĭ��Ϊ-o out");
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

		LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(fileInputStream);
		tokenList = lexicalAnalysis.lexicalAnalysis();
		viewTokenList(); // д��tokenList��output.txt
		SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(tokenList);
		syntaxAnalysis.syntaxAnalysis();

		try {
			fileInputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			System.err.println("�ļ����رճ���");
			System.exit(-1);
		}
	}

	private static void c(String inputPath, String outputPath) {
		System.out.println("�������c0Դ���뷭��Ϊ������Ŀ���ļ�");
		System.out.println("inputPath��" + inputPath);
		System.out.println("outputPath��" + outputPath);
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
}
