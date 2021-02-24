package qinfeng.zheng;


import java.util.concurrent.locks.LockSupport;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/24 20:48
 * @dec  LockSupport正式版，测试了一万次，没发现问题，应该是正确的
 */
public class T002_TwoThreadsPrint {


    private static class LetterThread extends Thread {
        private Thread numThread;
        private StringBuffer stringBuffer;

        public void setNumThread(Thread numThread) {
            this.numThread = numThread;
        }

        public void setStringBuffer(StringBuffer stringBuffer) {
            this.stringBuffer = stringBuffer;
        }

        @Override
        public void run() {
            for (int i = 65; i < 91; i++) {
//                System.out.print((char) i);
                stringBuffer.append((char) i);
                LockSupport.unpark(numThread);
                LockSupport.park();
            }
        }
    }


    private static class NumThread extends Thread {

        private Thread letterThread;
        private StringBuffer stringBuffer;

        public void setStringBuffer(StringBuffer stringBuffer) {
            this.stringBuffer = stringBuffer;
        }


        public void setLetterThread(Thread letterThread) {
            this.letterThread = letterThread;
        }

        /**
         * 先park后打印的线程一定要先挂起~
         */
        @Override
        public void run() {
            for (int i = 1; i <= 26; i++) {
                LockSupport.park();
//                System.out.print(i);
                stringBuffer.append(i);
                LockSupport.unpark(letterThread);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        boolean result = true;
        for (int i = 0; i < 10000; i++) {
            StringBuffer sb = new StringBuffer(52);
            LetterThread letterThread = new LetterThread();
            NumThread numThread = new NumThread();

            letterThread.setNumThread(numThread);
            letterThread.setStringBuffer(sb);
            numThread.setLetterThread(letterThread);
            numThread.setStringBuffer(sb);
            letterThread.start();
            numThread.start();
            numThread.join();
            letterThread.join();
            result =  sb.toString().equals("A1B2C3D4E5F6G7H8I9J10K11L12M13N14O15P16Q17R18S19T20U21V22W23X24Y25Z26");
            if (!result) {
                break;
            }
        }

        System.out.println(result);
    }
}
