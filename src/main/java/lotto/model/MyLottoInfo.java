package lotto.model;

import lotto.dto.PurchaseAmountDto;
import lotto.utils.CheckLotto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lotto.properties.LottoProperties.LOTTO_PRICE;
import static lotto.properties.LottoProperties.LOTTO_REVENUE_RATE;

public class MyLottoInfo {

    private final List<Lotto> myLotteries;
    private final int purchaseAmount;
    private final int purchaseLottoCount;
    private final Map<Rank, Integer> myResult;
    private int revenue;
    private double revenuePercentage;

    private MyLottoInfo(int purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
        this.purchaseLottoCount = calculateQuantities();
        this.myLotteries = generateLotto();
        this.myResult = initResult();
        this.revenue = 0;
    }

    public static MyLottoInfo from(PurchaseAmountDto dto){
        return new MyLottoInfo(
                dto.purchaseAmount()
        );
    }

    private Map<Rank, Integer> initResult(){
        Map<Rank, Integer> result = new LinkedHashMap<>();
        result.put(Rank.NONE, 0);
        result.put(Rank.FIFTH_PLACE, 0);
        result.put(Rank.FOURTH_PLACE, 0);
        result.put(Rank.THIRD_PLACE, 0);
        result.put(Rank.SECOND_PLACE, 0);
        result.put(Rank.FIRST_PLACE, 0);
        return result;
    }

    public void getResultPerLotto(WinningLotto winningLotto) {
        myLotteries.forEach(lotto ->
                lottoResult(
                        CheckLotto.countEqualLottoNumbers(lotto, winningLotto.getWinningLotto().getNumbers()),
                        CheckLotto.checkContainsBonusNumber(lotto, winningLotto.getBonusNumber())
                )
        );
    }

    private int calculateQuantities(){
        return this.purchaseAmount / LOTTO_PRICE;
    }

    private List<Lotto> generateLotto(){
        List<Lotto> lottos = new ArrayList<>();
        for(int i = 0; i < this.purchaseLottoCount; i++){
            lottos.add(Lotto.generate());
        }
        return lottos;
    }

    private void lottoResult(int count, boolean isBonusNumberMatch) {
        Rank rank = determineRank(count, isBonusNumberMatch);
        updateRevenue(rank);
        updateResult(rank);
    }

    private Rank determineRank(int count, boolean isBonusNumberMatch) {
        return Rank.findRank(count, isBonusNumberMatch);
    }

    private void updateRevenue(Rank rank) {
        revenue += rank.getWinningPrice();
    }

    private void updateResult(Rank rank) {
        myResult.put(rank, myResult.get(rank) + 1);
    }

    public List<Lotto> getMyLotteries() {
        return myLotteries;
    }

    public int getPurchaseLottoCount() {
        return purchaseLottoCount;
    }
    public double getRevenuePercentage() {
        revenuePercentage = ((double) revenue / (double) purchaseAmount) * LOTTO_REVENUE_RATE;
        return revenuePercentage;
    }

    public Map<Rank, Integer> getMyResult() {
        return myResult;
    }
}
