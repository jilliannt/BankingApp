package bankapp;


public class CheckingAccount extends BankAccount {

    public CheckingAccount(String accountName) {
        super(accountName);
        setTransferLimit(2000.0); // Higher limit for checking accounts
    }

    @Override
    public String getAccountType() {
        return "checking";
    }

    public void orderChecks() {
        System.out.println("Checks ordered for your checking account.");
    }

    public String orderDebitCard() {
        System.out.println();
        System.out.println("Debit card ordered for your checking account.");
        System.out.println("Please allow 5-7 business days for delivery.");
        String debitCardNumber = debitCardNumberGenerator();
        System.out.println("Your debit card number is: " + debitCardNumber);
        String expirationDate = debitCardExpirationDateGenerator();
        System.out.println("Your debit card expiration date is: " + expirationDate);
        System.out.println("Please contact us to activate your card once it arrives.");
        String lastFour = debitCardNumber.substring(debitCardNumber.length() - 4);
        return lastFour;
    }

    private String debitCardNumberGenerator() {
        StringBuilder cardNumber = new StringBuilder("2025");
        for (int i = 0; i < 12; i++) {
            if (i % 4 == 0) {
                cardNumber.append("-");
            }
            cardNumber.append((int) (Math.random() * 10));
        }
        return cardNumber.toString();
    }

    private String debitCardExpirationDateGenerator() {
        StringBuilder expirationDate = new StringBuilder();
        int month = (int) (Math.random() * 12) + 1;
        int year = 2028;
        expirationDate.append(String.format("%02d/%d", month, year));
        return expirationDate.toString();
    }


}
