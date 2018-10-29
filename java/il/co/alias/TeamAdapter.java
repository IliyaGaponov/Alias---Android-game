package il.co.alias;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import static il.co.alias.ConstantsHolder.*;


import java.util.List;

/**
 * Created by igapo on 01.10.2018.
 */

public class TeamAdapter extends RecyclerView.Adapter {

    private List<String> teams;
    private List<String> scores;
    private TeamCardTypeEnum itemType;
    private TeamHolderFactory.NewTeamHolder.IAddTeamListener listener;

    public void setListener(TeamHolderFactory.NewTeamHolder.IAddTeamListener listener) {
        this.listener = listener;
    }

    public TeamAdapter(List<String> teams, TeamCardTypeEnum cardType) {
        this.teams = teams;
        itemType = cardType;
    }

    public TeamAdapter(List<String> teams, List<String> scores, TeamCardTypeEnum itemType) {
        this.teams = teams;
        this.scores = scores;
        this.itemType = itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TeamHolderFactory.create(parent, itemType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String team = teams.get(position);

        switch (itemType)
        {
            case NEW_TEAM_CARD:
                ((TeamHolderFactory.NewTeamHolder)holder).getTeamName().setText(team);
                ((TeamHolderFactory.NewTeamHolder)holder).setListener(listener);
                break;
            case TEAM_RESULT_CARD:
                String score = scores.get(position);
                ((TeamHolderFactory.TeamResultHolder)holder).teamName.setText(team);
                ((TeamHolderFactory.TeamResultHolder)holder).teamScore.setText(score);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }
}
