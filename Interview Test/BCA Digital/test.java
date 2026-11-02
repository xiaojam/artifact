/*
1. Buatlah fungsi dengan parameter berapa banyak angka yg akan ditampilkan secara looping, dimana jika angka tersebut habis dibagi 3 cetak "fish" jika habis dibagi 5 cetak "bash" dan jika habis dibagi 15 cetak "fish bash", cetak cukup salah satu dari kondisi tersebut, cetak menggunakan console log atau sejenisnya.
 
2. Buatlah fungsi dengan input array integer untuk mengurutkan angka dari kecil kebesar dan besar kekecil dengan manual tanpa fungsi bawaan javascript,
 
3. Buatlah fungsi dengan input string jika string tersebut dibalik dan tetap sama dengan string yang diinputkan maka return true jika tidak return false
*/

class Main {
    public static void main(String[] args) {
        System.out.println("Try clicking the Run button.");

     int n = 15;
        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0) {
                System.out.println("Fish Bash");
            } else if (i % 3 == 0) {
                System.out.println("Fish");
            } else if (i % 5 == 0) {
                System.out.println("Bash");
            } else {
                System.out.println(i);
            }
        }

        int[] arr = {10, 3, 6, 7};

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] < arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();

        String str = "Nababan";
        String strClean = str.toLowerCase();
        boolean isPalindrome = true;
        int left = 0;
        int right = strClean.length() - 1;

        while (left < right) {
            if (strClean.charAt(left) != strClean.charAt(right)) {
                isPalindrome = false;
                break;
            }
            left++;
            right--;
        }

        System.out.println(isPalindrome);
    }
}