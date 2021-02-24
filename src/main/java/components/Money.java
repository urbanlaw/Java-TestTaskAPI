package components;

/**
 * local representation of money
 */
public class Money
{
    private int cents;

    public Money(int cents)
    {
        this.cents = cents;
    }

    public boolean withdraw(int value)
    {
        if(this.isEnough(value))
        {
            this.cents -= value;
            return true;
        }
        return false;
    }

    public boolean isEnough(Money money)
    {
        return this.cents >= money.cents;
    }

    public boolean isEnough(int value)
    {
        return this.cents >= value;
    }

    @Override
    public String toString()
    {
        Double money = (double)this.cents / 100;
        return String.format("%.2f", money);
    }
}
